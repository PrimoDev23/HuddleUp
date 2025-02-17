package dev.primodev.huddleup.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.primodev.huddleup.domain.entity.event.Event
import dev.primodev.huddleup.domain.entity.event.EventDuration
import kotlinx.datetime.Instant
import kotlin.uuid.Uuid

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey
    val id: Uuid,
    val duration: EventDuration,
    val start: Instant,
    val end: Instant,
    val title: String,
)

internal fun Event.toEntity() = EventEntity(
    id = id,
    duration = duration,
    start = start,
    end = end,
    title = title
)

internal fun EventEntity.toDomain() = Event(
    id = id,
    duration = duration,
    start = start,
    end = end,
    title = title
)