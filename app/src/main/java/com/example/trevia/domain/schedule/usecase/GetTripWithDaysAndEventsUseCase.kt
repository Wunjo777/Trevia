package com.example.trevia.domain.schedule.usecase

import com.example.trevia.data.local.schedule.DayRepository
import com.example.trevia.data.local.schedule.EventRepository
import com.example.trevia.data.local.schedule.TripRepository
import com.example.trevia.di.OfflineRepo
import com.example.trevia.domain.schedule.model.DayWithEventsModel
import com.example.trevia.domain.schedule.model.TripWithDaysAndEventsModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Singleton

@Singleton
class GetTripWithDaysAndEventsUseCase @Inject constructor(
    @OfflineRepo private val tripRepository: TripRepository,
    private val dayRepository: DayRepository,
    private val eventRepository: EventRepository
)
{

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(tripId: Long): Flow<TripWithDaysAndEventsModel?>
    {

        val tripModelFlow = tripRepository.getTripByIdStream(tripId)

        val dayModelFlow = dayRepository.getDaysByTripId(tripId)

        val daysWithEventsFlow: Flow<List<DayWithEventsModel>> =
            dayModelFlow.flatMapLatest { days ->// days: List<DayModel>

                if (days.isEmpty())
                {
                    // 没有 Day 时直接发射空列表
                    return@flatMapLatest kotlinx.coroutines.flow.flowOf(emptyList())
                }

                // 为每个 Day 创建一个 Flow<DayWithEvents>
                val dayWithEventsFlows: List<Flow<DayWithEventsModel>> =
                    days.map { day ->//day: DayModel to Flow<DayWithEventsModel>
                        eventRepository.getEventsByDayId(day.id) // Flow<List<Event>> to Flow<DayWithEventsModel>
                            .map { events ->
                                DayWithEventsModel(
                                    id = day.id,
                                    tripId = day.tripId,
                                    date = day.date,
                                    indexInTrip = day.indexInTrip,
                                    events = events
                                )
                            }
                    }

                // combine 所有 DayWithEvents Flow => Flow<List<DayWithEventsModel>>
                combine(dayWithEventsFlows) { array -> array.toList() }
            }

        // 最终将 Trip + DaysWithEvents 合并
        return combine(tripModelFlow, daysWithEventsFlow) { trip, daysWithEvents ->
            trip?.let {
                TripWithDaysAndEventsModel(
                    id = it.id,
                    name = it.name,
                    destination = it.destination,
                    startDate = it.startDate,
                    endDate = it.endDate,
                    daysWithEvents = daysWithEvents
                )
            }
        }
    }
}
