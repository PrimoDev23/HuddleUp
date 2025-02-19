package dev.primodev.huddleup.feature.eventcreation.uistate

import dev.primodev.huddleup.domain.entity.event.EventDuration
import kotlinx.datetime.LocalDateTime

data class EventCreationInputState(
    val title: String,
    val duration: EventDuration,
    val start: LocalDateTime,
    val end: LocalDateTime,
)