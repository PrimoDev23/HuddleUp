package dev.primodev.huddleup.data.database.converters

import androidx.room.TypeConverter
import kotlin.uuid.Uuid

class UuidConverter {

    @TypeConverter
    fun fromUuidToString(uuid: Uuid) = uuid.toHexString()

    @TypeConverter
    fun fromStringToUuid(hex: String) = Uuid.parseHex(hex)

}