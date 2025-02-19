package dev.primodev.huddleup.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.primodev.huddleup.data.extensions.toZone
import dev.primodev.huddleup.domain.entity.event.Event
import dev.primodev.huddleup.domain.entity.event.EventDuration
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlin.uuid.Uuid

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey
    val id: Uuid,
    val duration: EventDuration,
    val timeZone: TimeZone,
    val start: LocalDateTime,
    val end: LocalDateTime,
    val title: String,
)

internal fun Event.toEntity() = EventEntity(
    id = id,
    duration = duration,
    timeZone = TimeZone.currentSystemDefault(),
    start = start,
    end = end,
    title = title
)

internal fun EventEntity.toDomain() = Event(
    id = id,
    duration = duration,
    start = start.toZone(from = timeZone, to = TimeZone.currentSystemDefault()),
    end = end.toZone(from = timeZone, to = TimeZone.currentSystemDefault()),
    title = title
)