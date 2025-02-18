package dev.primodev.huddleup.feature.home.uistate

import dev.primodev.huddleup.domain.entity.event.Event
import dev.primodev.huddleup.domain.entity.event.EventDuration
import dev.primodev.huddleup.extensions.atTime
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.days

internal fun Event.toEventsPerDay(): List<Event> {
    return buildList {
        var currentDate = start
        val endDate = end

        do {
            val newEvent = when (duration) {
                EventDuration.AllDay -> {
                    this@toEventsPerDay.copy(
                        duration = EventDuration.AllDay,
                        start = currentDate,
                        end = currentDate
                    )
                }

                EventDuration.Specific -> {
                    val newDuration = getEventDurationForSpecificDuration(
                        start = start,
                        end = end,
                        date = currentDate
                    )

                    this@toEventsPerDay.copy(
                        duration = newDuration.duration,
                        start = newDuration.start,
                        end = newDuration.end
                    )
                }
            }

            add(newEvent)

            currentDate = currentDate.plus(1.days)
        } while (currentDate <= endDate)
    }
}

private fun getEventDurationForSpecificDuration(
    start: Instant,
    end: Instant,
    date: Instant,
): NewEventDuration {
    val startDateTime = start.toLocalDateTime(TimeZone.UTC)
    val endDateTime = end.toLocalDateTime(TimeZone.UTC)
    val currentDate = date.toLocalDateTime(TimeZone.UTC).date

    return if (startDateTime.date < currentDate && endDateTime.date > currentDate) {
        NewEventDuration(
            duration = EventDuration.AllDay,
            start = date,
            end = date
        )
    } else {
        val startTime = if (startDateTime.date == currentDate) {
            startDateTime.time
        } else {
            LocalTime(
                hour = 0,
                minute = 0,
                second = 0
            )
        }
        val endTime = if (endDateTime.date == currentDate) {
            endDateTime.time
        } else {
            LocalTime(
                hour = 23,
                minute = 59,
                second = 59
            )
        }

        NewEventDuration(
            duration = EventDuration.Specific,
            start = date.atTime(startTime),
            end = date.atTime(endTime)
        )
    }
}

private data class NewEventDuration(
    val duration: EventDuration,
    val start: Instant,
    val end: Instant,
)