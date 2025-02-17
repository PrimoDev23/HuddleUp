package dev.primodev.huddleup.feature.eventcreation

import kotlinx.datetime.Instant

sealed interface EventCreationUiEvent {
    data class TitleChange(val title: String) : EventCreationUiEvent
    data class AllDayCheckedChange(val checked: Boolean) : EventCreationUiEvent
    data class CurrentDateTimeDialogChange(val dialog: EventCreationDialog) :
        EventCreationUiEvent

    data class StartChanged(val instant: Instant) : EventCreationUiEvent
    data class EndChanged(val instant: Instant) : EventCreationUiEvent
    data object SaveClick : EventCreationUiEvent
}