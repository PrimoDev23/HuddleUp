package dev.primodev.huddleup.data.database.converters

import androidx.room.TypeConverter
import kotlinx.datetime.Instant

class InstantConverter {

    @TypeConverter
    fun fromInstantToLong(instant: Instant): Long = instant.toEpochMilliseconds()

    @TypeConverter
    fun fromLongToInstant(long: Long): Instant = Instant.fromEpochMilliseconds(long)

}