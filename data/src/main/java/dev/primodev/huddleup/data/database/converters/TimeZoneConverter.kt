package dev.primodev.huddleup.data.database.converters

import androidx.room.TypeConverter
import kotlinx.datetime.TimeZone

class TimeZoneConverter {

    @TypeConverter
    fun fromTimeZoneToString(zone: TimeZone) = zone.id

    @TypeConverter
    fun fromStringToTimeZone(id: String) = TimeZone.of(id)

}