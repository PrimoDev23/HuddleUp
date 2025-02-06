package dev.primodev.huddleup.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.primodev.huddleup.domain.entity.event.Event
import dev.primodev.huddleup.domain.entity.event.EventDuration
import dev.primodev.huddleup.extensions.nowAsDateTime
import dev.primodev.huddleup.feature.home.uistate.HomeUiState
import dev.primodev.huddleup.feature.home.uistate.toEventsPerDay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.atDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

class HomeViewModel : ViewModel() {

    private val selectedDate = MutableStateFlow(Clock.System.nowAsDateTime().date)

    private val events = flowOf(
        listOf(
            Event(
                duration = EventDuration.AllDay(
                    start = Clock.System.nowAsDateTime().date,
                    end = Clock.System.nowAsDateTime().date.plus(
                        value = 1,
                        unit = DateTimeUnit.DAY
                    )
                ),
                title = "All day event"
            ),
            Event(
                duration = EventDuration.Specific(
                    start = Clock.System.nowAsDateTime().let { dateTime ->
                        LocalTime(13, 13).atDate(dateTime.date.minus(1, DateTimeUnit.DAY))
                    },
                    end = Clock.System.nowAsDateTime().let { dateTime ->
                        LocalTime(16, 16).atDate(dateTime.date.plus(1, DateTimeUnit.DAY))
                    }
                ),
                title = "Specific event multiple days"
            ),
            Event(
                duration = EventDuration.Specific(
                    start = Clock.System.nowAsDateTime().let { dateTime ->
                        LocalTime(13, 13).atDate(dateTime.date)
                    },
                    end = Clock.System.nowAsDateTime().let { dateTime ->
                        LocalTime(16, 16).atDate(dateTime.date)
                    }
                ),
                title = "Specific event same day"
            ),
        )
    )

    val uiState = combine(
        selectedDate,
        events
    ) { selectedDate, events ->
        HomeUiState.Data(
            events = events.prepareEventMap(),
            selectedDate = selectedDate
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = HomeUiState.InitLoading
    )

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            is HomeUiEvent.DayClick -> onDayClick(event.date)
            HomeUiEvent.AddEventClick -> onAddEventClick()
        }
    }

    private fun onDayClick(date: LocalDate) {
        viewModelScope.launch {
            selectedDate.emit(date)
        }
    }

    private fun onAddEventClick() {

    }

    private fun List<Event>.prepareEventMap() =
        this
            .flatMap { event ->
                event.toEventsPerDay()
            }
            .groupBy { event ->
                when (val duration = event.duration) {
                    is EventDuration.AllDay -> duration.start
                    is EventDuration.Specific -> duration.start.date
                }
            }
}