package dev.primodev.huddleup.feature.home.uistate

import dev.primodev.huddleup.domain.entity.event.Event
import kotlinx.datetime.LocalDate

sealed interface HomeUiState {
    data object InitLoading : HomeUiState
    data object Error : HomeUiState
    data class Data(
        val events: Map<LocalDate, List<Event>>,
        val selectedDate: LocalDate,
    ) : HomeUiState
}