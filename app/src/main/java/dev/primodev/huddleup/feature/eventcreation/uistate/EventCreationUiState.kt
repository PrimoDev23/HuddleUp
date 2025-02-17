package dev.primodev.huddleup.feature.eventcreation.uistate

import dev.primodev.huddleup.feature.eventcreation.EventCreationDialog
import kotlinx.datetime.Instant

data class EventCreationUiState(
    val title: String,
    val allDayChecked: Boolean,
    val start: Instant,
    val end: Instant,
    val eventCreationDialog: EventCreationDialog,
)