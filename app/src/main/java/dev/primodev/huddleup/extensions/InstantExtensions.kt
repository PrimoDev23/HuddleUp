package dev.primodev.huddleup.extensions

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

fun Instant.atTime(time: LocalTime) =
    this.toLocalDateTime(TimeZone.UTC).date.atTime(time).toInstant(TimeZone.UTC)

fun Instant.atTime(hour: Int, minute: Int) =
    this
        .toLocalDateTime(TimeZone.UTC)
        .date
        .atTime(
            hour = hour,
            minute = minute
        )
        .toInstant(TimeZone.UTC)