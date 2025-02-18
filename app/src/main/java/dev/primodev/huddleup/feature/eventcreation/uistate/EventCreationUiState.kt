package dev.primodev.huddleup.feature.eventcreation.uistate

import dev.primodev.huddleup.domain.entity.event.EventDuration
import dev.primodev.huddleup.feature.eventcreation.EventCreationDialog
import kotlinx.datetime.Instant

data class EventCreationUiState(
    val title: String,
    val duration: EventDuration,
    val start: Instant,
    val end: Instant,
    val currentDialog: EventCreationDialog,
    val uiError: EventCreationUiError?,
)