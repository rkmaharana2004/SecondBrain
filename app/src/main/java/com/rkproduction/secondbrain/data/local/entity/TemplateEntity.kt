package com.rkproduction.secondbrain.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
data class TemplateField(
    val label: String,
    val hint: String,
    val fieldType: String // TEXT, NUMBER, DATE, CHECKBOX
)

@Entity(tableName = "templates")
data class TemplateEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val icon: String,
    val fields: List<TemplateField>
)
