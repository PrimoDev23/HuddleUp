package dev.primodev.huddleup.feature.eventcreation.uistate

import dev.primodev.huddleup.domain.entity.event.EventDuration
import dev.primodev.huddleup.feature.eventcreation.EventCreationDialog
import kotlinx.datetime.LocalDateTime

data class EventCreationUiState(
    val currentDialog: EventCreationDialog,
    val contentState: EventCreationContentState,
)

data class EventCreationContentState(
    val title: String,
    val duration: EventDuration,
    val start: LocalDateTime,
    val end: LocalDateTime,
    val uiError: EventCreationUiError?,
)