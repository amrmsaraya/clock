package com.github.amrmsaraya.clock.database.converter

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converter {

    @TypeConverter
    fun fromIntList(list: List<Int>): String {
        return Json.encodeToString(list)
    }

    @TypeConverter
    fun toListInt(string: String): List<Int> {
        return Json.decodeFromString(string)
    }
}