package dev.primodev.huddleup.domain.entity.event

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

sealed interface EventDuration {
    data class AllDay(val date: LocalDate) : EventDuration
    data class Specific(
        val start: LocalDateTime,
        val end: LocalDateTime,
    ) : EventDuration
}
