package dev.primodev.huddleup.domain.entity.event

import kotlin.uuid.Uuid

data class Event(
    val id: Uuid = Uuid.random(),
    val duration: EventDuration,
    val title: String,
)