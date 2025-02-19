package dev.primodev.huddleup.data.extensions

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

fun LocalDateTime.toZone(from: TimeZone, to: TimeZone) = if (from == to) {
    this
} else {
    this.toInstant(from).toLocalDateTime(to)
}