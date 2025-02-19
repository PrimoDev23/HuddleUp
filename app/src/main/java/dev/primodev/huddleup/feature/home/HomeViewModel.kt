package dev.primodev.huddleup.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.primodev.huddleup.appresult.AppResult
import dev.primodev.huddleup.domain.entity.event.Event
import dev.primodev.huddleup.domain.usecase.event.DeleteEventByIdUseCase
import dev.primodev.huddleup.domain.usecase.event.GetAllEventsUseCase
import dev.primodev.huddleup.extensions.nowAsDateTime
import dev.primodev.huddleup.feature.eventcreation.EventCreationDestination
import dev.primodev.huddleup.feature.home.uistate.HomeUiState
import dev.primodev.huddleup.feature.home.uistate.toEventsPerDay
import dev.primodev.huddleup.navigation.AppNavigator
import dev.primodev.huddleup.triggerables.submittable.instantSubmittable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate

class HomeViewModel(
    getAllEventsUseCase: GetAllEventsUseCase,
    deleteEventByIdUseCase: DeleteEventByIdUseCase,
    private val navigator: AppNavigator,
) : ViewModel() {

    private val selectedDate = MutableStateFlow(Clock.System.nowAsDateTime().date)
    private val events = getAllEventsUseCase.execute()

    private val deleteSubmittable = instantSubmittable<Event, Unit> { event ->
        deleteEventByIdUseCase.execute(event.id)
    }

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

    init {
        // TODO: Do not ignore result
        deleteSubmittable.flow.launchIn(viewModelScope)
    }

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            HomeUiEvent.TodayClick -> onTodayClick()
            is HomeUiEvent.DayClick -> onDayClick(event.date)
            HomeUiEvent.AddEventClick -> onAddEventClick()
            is HomeUiEvent.DeleteSwiped -> onDeleteSwiped(event.event)
        }
    }

    private fun onTodayClick() {
        viewModelScope.launch {
            selectedDate.emit(Clock.System.nowAsDateTime().date)
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

    private fun onDeleteSwiped(event: Event) {
        viewModelScope.launch {
            deleteSubmittable.submit(event)
        }
    }

    private fun List<Event>.prepareEventMap() =
        this
            .flatMap { event ->
                event.toEventsPerDay()
            }
            .groupBy { event ->
                event.start.date
            }
}