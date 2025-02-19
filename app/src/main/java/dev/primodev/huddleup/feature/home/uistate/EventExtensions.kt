package dev.primodev.huddleup.feature.home.uistate

import dev.primodev.huddleup.domain.entity.event.Event
import dev.primodev.huddleup.domain.entity.event.EventDuration
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.atTime
import kotlinx.datetime.plus

internal fun Event.toEventsPerDay(): List<Event> {
    return buildList {
        var currentDate = start.date
        val endDate = end.date

        do {
            val newEvent = when (duration) {
                EventDuration.AllDay -> {
                    this@toEventsPerDay.copy(
                        duration = EventDuration.AllDay,
                        start = currentDate.atTime(
                            hour = 0,
                            minute = 0
                        ),
                        end = currentDate.atTime(
                            hour = 23,
                            minute = 59,
                            second = 59
                        )
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

            currentDate = currentDate.plus(
                value = 1,
                unit = DateTimeUnit.DAY
            )
        } while (currentDate <= endDate)
    }
}

private fun getEventDurationForSpecificDuration(
    start: LocalDateTime,
    end: LocalDateTime,
    date: LocalDate,
): NewEventDuration {
    return if (start.date < date && end.date > date) {
        NewEventDuration(
            duration = EventDuration.AllDay,
            start = date.atTime(
                hour = 0,
                minute = 0
            ),
            end = date.atTime(
                hour = 23,
                minute = 59,
                second = 59
            )
        )
    } else {
        val startTime = if (start.date == date) {
            start.time
        } else {
            LocalTime(
                hour = 0,
                minute = 0,
                second = 0
            )
        }
        val endTime = if (end.date == date) {
            end.time
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
    val start: LocalDateTime,
    val end: LocalDateTime,
)