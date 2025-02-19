package dev.primodev.huddleup.data.database.converters

import androidx.room.TypeConverter
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

class LocalDateTimeConverter {

    @TypeConverter
    fun fromLocalDateTimeToLong(dateTime: LocalDateTime): Long =
        dateTime.toInstant(TimeZone.UTC).toEpochMilliseconds()

    @TypeConverter
    fun fromLongToLocalDateTime(long: Long): LocalDateTime =
        Instant.fromEpochMilliseconds(long).toLocalDateTime(TimeZone.UTC)

}