package dev.primodev.huddleup.domain.entity.event

import kotlinx.datetime.LocalDateTime
import kotlin.uuid.Uuid

data class Event(
    val id: Uuid = Uuid.random(),
    val duration: EventDuration,
    val start: LocalDateTime,
    val end: LocalDateTime,
    val title: String,
)