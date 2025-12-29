package com.example.trevia.domain.location

//import kotlinx.coroutines.supervisorScope
import com.example.trevia.data.remote.PoiEnvRepository
import com.example.trevia.data.remote.amap.PoiWeatherRepository
import com.example.trevia.domain.location.decision.DecidePoiDataUseCase
import com.example.trevia.domain.location.model.DegradeReason
import com.example.trevia.domain.location.model.LocationInputs
import com.example.trevia.domain.location.model.ModuleState
import com.example.trevia.domain.location.model.PoiDecision
import com.example.trevia.ui.location.PoiDetailUiState
import com.example.trevia.ui.location.WeatherUiState
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationDetailOrchestratorUseCase @Inject constructor(
    private val poiWeatherRepository: PoiWeatherRepository,
    private val poiEnvRepository: PoiEnvRepository,
    private val decidePoiDataUseCase: DecidePoiDataUseCase,
    private val defaultTimeoutMs: Long = 3000
)
{

    suspend fun loadModules(poiId: String): LocationDetailModules =
        supervisorScope {

            val decision = withTimeoutOrNull(defaultTimeoutMs) {
                val poiRaw = poiWeatherRepository.getPoiWithWeather(poiId)

                val input = LocationInputs(
                    poiDetail = poiRaw.poi,
                    weather = poiRaw.weather,
                    isVisible = poiEnvRepository.isVisible(),
                    networkAvailable = poiEnvRepository.isNetworkAvailable(),
                    userPrefShowWeather = false
                )
                decidePoiDataUseCase(input)
            }

            // ⛔ 超时：两个模块一起降级
            if (decision == null)
            {
                return@supervisorScope LocationDetailModules(
                    poi = ModuleState.Degraded<PoiDetailUiState>(
                        data = null,
                        reason = DegradeReason.TIMEOUT
                    ),
                    weather = ModuleState.Degraded<WeatherUiState>(
                        data = null,
                        reason = DegradeReason.TIMEOUT
                    )
                )
            }

            val poiModule = buildPoiModule(decision)
            val weatherModule = buildWeatherModule(decision)

            return@supervisorScope LocationDetailModules(
                poi = poiModule,
                weather = weatherModule
            )
        }

    private fun buildPoiModule(decision: PoiDecision): ModuleState<PoiDetailUiState>
    {
        val uiData = PoiDetailUiState(
            poi = decision.poi,
            showPoiInfo = decision.showPoiInfo,
            poiTextLevel = decision.poiTextLevel,
            degradeReason = decision.degradeReason
        )

        return if (decision.degradeReason == null)
        {
            ModuleState.Success(uiData)
        }
        else
        {
            ModuleState.Degraded(
                data = if (decision.showPoiInfo && decision.poi != null) uiData else null,
                reason = decision.degradeReason
            )
        }
    }

    private fun buildWeatherModule(decision: PoiDecision): ModuleState<WeatherUiState>
    {
        val uiData = WeatherUiState(
            weather = decision.weather,
            showWeather = decision.showWeather,
            degradeReason = decision.degradeReason
        )

        return if (decision.degradeReason == null)
        {
            ModuleState.Success(uiData)
        }
        else
        {
            ModuleState.Degraded(
                data = if (decision.showWeather && decision.weather != null) uiData else null,
                reason = decision.degradeReason
            )
        }
    }
}

data class LocationDetailModules(
    val poi: ModuleState<PoiDetailUiState>,
    val weather: ModuleState<WeatherUiState>
)



