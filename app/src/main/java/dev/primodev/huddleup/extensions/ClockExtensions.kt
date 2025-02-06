package dev.primodev.huddleup.extensions

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

internal fun Clock.System.nowAsDateTime(timeZone: TimeZone = TimeZone.currentSystemDefault()) =
    now().toLocalDateTime(timeZone)