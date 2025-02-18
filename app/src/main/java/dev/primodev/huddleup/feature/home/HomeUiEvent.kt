package dev.primodev.huddleup.feature.home

import dev.primodev.huddleup.domain.entity.event.Event
import kotlinx.datetime.LocalDate

sealed interface HomeUiEvent {
    data object TodayClick : HomeUiEvent
    data class DayClick(val date: LocalDate) : HomeUiEvent
    data object AddEventClick : HomeUiEvent

    data class DeleteSwiped(val event: Event) : HomeUiEvent
}