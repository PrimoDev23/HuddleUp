package dev.primodev.huddleup.domain.entity.event

import kotlinx.datetime.Instant
import kotlin.uuid.Uuid

data class Event(
    val id: Uuid = Uuid.random(),
    val duration: EventDuration,
    val start: Instant,
    val end: Instant,
    val title: String,
)