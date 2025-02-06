package dev.primodev.huddleup.feature.home.uistate

import dev.primodev.huddleup.domain.entity.event.Event
import dev.primodev.huddleup.domain.entity.event.EventDuration
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.atTime
import kotlinx.datetime.plus

internal fun Event.toEventsPerDay() {
    when (val duration = this.duration) {
        is EventDuration.AllDay -> {
            listOf(this)
        }

        is EventDuration.Specific -> buildList {
            var currentDate = duration.start.date

            do {
                val newDuration = getEventDurationForDay(
                    start = duration.start,
                    end = duration.end,
                    date = currentDate
                )

                add(
                    this@toEventsPerDay.copy(
                        duration = newDuration
                    )
                )

                currentDate = currentDate.plus(
                    value = 1,
                    unit = DateTimeUnit.DAY
                )
            } while (currentDate <= duration.end.date)
        }
    }
}

private fun getEventDurationForDay(
    start: LocalDateTime,
    end: LocalDateTime,
    date: LocalDate,
): EventDuration {
    return if (start.date < date && end.date > date) {
        EventDuration.AllDay(
            date = date
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

        EventDuration.Specific(
            start = date.atTime(startTime),
            end = date.atTime(endTime)
        )
    }
}