package com.rkproduction.secondbrain.data.local.db

import androidx.room.TypeConverter
import com.rkproduction.secondbrain.data.local.entity.TemplateField
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromTemplateFields(value: List<TemplateField>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toTemplateFields(value: String): List<TemplateField> {
        return Json.decodeFromString(value)
    }
}
