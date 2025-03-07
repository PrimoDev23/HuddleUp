package dev.primodev.huddleup.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.primodev.huddleup.data.dao.EventDao
import dev.primodev.huddleup.data.database.converters.LocalDateTimeConverter
import dev.primodev.huddleup.data.database.converters.TimeZoneConverter
import dev.primodev.huddleup.data.database.converters.UuidConverter
import dev.primodev.huddleup.data.entity.EventEntity

@Database(
    entities = [
        EventEntity::class
    ],
    version = 1
)
@TypeConverters(
    LocalDateTimeConverter::class,
    TimeZoneConverter::class,
    UuidConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
}