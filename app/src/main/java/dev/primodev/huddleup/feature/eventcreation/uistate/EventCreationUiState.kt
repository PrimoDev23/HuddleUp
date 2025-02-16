package dev.primodev.huddleup.feature.eventcreation.uistate

import dev.primodev.huddleup.feature.eventcreation.CurrentDateTimePickerDialog
import kotlinx.datetime.Instant

data class EventCreationUiState(
    val title: String,
    val allDayChecked: Boolean,
    val start: Instant,
    val end: Instant,
    val currentDateTimePickerDialog: CurrentDateTimePickerDialog,
)