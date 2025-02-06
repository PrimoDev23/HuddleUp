package dev.primodev.huddleup.feature.home

import kotlinx.datetime.LocalDate

sealed interface HomeUiEvent {
    data class DayClick(val date: LocalDate) : HomeUiEvent
    data object AddEventClick : HomeUiEvent
}