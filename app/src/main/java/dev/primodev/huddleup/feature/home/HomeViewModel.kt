package dev.primodev.huddleup.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.primodev.huddleup.appresult.AppResult
import dev.primodev.huddleup.domain.entity.event.Event
import dev.primodev.huddleup.domain.usecase.event.GetAllEventsUseCase
import dev.primodev.huddleup.extensions.nowAsDateTime
import dev.primodev.huddleup.feature.eventcreation.EventCreationDestination
import dev.primodev.huddleup.feature.home.uistate.HomeUiState
import dev.primodev.huddleup.feature.home.uistate.toEventsPerDay
import dev.primodev.huddleup.navigation.AppNavigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class HomeViewModel(
    private val getAllEventsUseCase: GetAllEventsUseCase,
    private val navigator: AppNavigator,
) : ViewModel() {

    private val selectedDate = MutableStateFlow(Clock.System.nowAsDateTime().date)

    private val events = getAllEventsUseCase.execute()

    val uiState = combine(
        selectedDate,
        events
    ) { selectedDate, eventsResult ->
        when (eventsResult) {
            AppResult.Loading -> HomeUiState.InitLoading
            is AppResult.Error -> HomeUiState.Error
            is AppResult.Success -> HomeUiState.Data(
                events = eventsResult.value.prepareEventMap(),
                selectedDate = selectedDate
            )
        }
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
        viewModelScope.launch {
            navigator.navigateTo(EventCreationDestination)
        }
    }

    private fun List<Event>.prepareEventMap() =
        this
            .flatMap { event ->
                event.toEventsPerDay()
            }
            .groupBy { event ->
                event.start.toLocalDateTime(TimeZone.UTC).date
            }
}