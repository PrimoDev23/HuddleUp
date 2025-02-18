package dev.primodev.huddleup.feature.eventcreation

import dev.primodev.huddleup.domain.entity.event.EventDuration
import kotlinx.datetime.Instant

sealed interface EventCreationUiEvent {
    data class TitleChange(val title: String) : EventCreationUiEvent
    data class DurationChanged(val duration: EventDuration) : EventCreationUiEvent
    data class CurrentDateTimeDialogChange(val dialog: EventCreationDialog) :
        EventCreationUiEvent

    data class StartChanged(val instant: Instant) : EventCreationUiEvent
    data class EndChanged(val instant: Instant) : EventCreationUiEvent
    data object SaveClick : EventCreationUiEvent
}