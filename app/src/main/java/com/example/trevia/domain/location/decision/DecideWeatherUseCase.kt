package com.example.trevia.domain.location.decision

import com.example.trevia.domain.location.model.WeatherDecision
import com.example.trevia.domain.location.model.WeatherInputs
import javax.inject.Inject
import javax.inject.Singleton
import com.example.trevia.domain.location.model.DegradeReason

@Singleton
class DecideWeatherUseCase @Inject constructor()
{

    suspend operator fun invoke(input: WeatherInputs): WeatherDecision
    {
        val canShowWeather =
            input.userPrefShowWeather &&
                    input.weather != null &&
                    input.networkAvailable

        return if (canShowWeather)
        {
            WeatherDecision(
                weather = input.weather,
                showWeather = true,
                degradeReason = null
            )
        }
        else
        {
            WeatherDecision(
                weather = null,
                showWeather = false,
                degradeReason = when
                {
                    !input.userPrefShowWeather -> null
                    input.weather == null      -> DegradeReason.UNAVAILABLE
                    else                       -> DegradeReason.NOT_VISIBLE
                }
            )
        }
    }
}
