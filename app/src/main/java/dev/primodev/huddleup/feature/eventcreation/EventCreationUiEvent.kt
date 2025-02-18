package dev.primodev.huddleup.feature.eventcreation

import dev.primodev.huddleup.domain.entity.event.EventDuration
import kotlinx.datetime.Instant

sealed interface EventCreationUiEvent {
    data object Back : EventCreationUiEvent

    data class TitleChange(val title: String) : EventCreationUiEvent
    data class DurationChanged(val duration: EventDuration) : EventCreationUiEvent

    data object StartDateClick : EventCreationUiEvent
    data object StartTimeClick : EventCreationUiEvent
    data object EndDateClick : EventCreationUiEvent
    data object EndTimeClick : EventCreationUiEvent
    data object DialogDismissed : EventCreationUiEvent

    data class StartChanged(val instant: Instant) : EventCreationUiEvent
    data class EndChanged(val instant: Instant) : EventCreationUiEvent
    data object SaveClick : EventCreationUiEvent
    data object DiscardClick : EventCreationUiEvent
}