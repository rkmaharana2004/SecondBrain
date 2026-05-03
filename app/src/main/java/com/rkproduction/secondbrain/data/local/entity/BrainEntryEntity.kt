package com.rkproduction.secondbrain.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "brain_entries")
data class BrainEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val type: String, // e.g., "NOTE", "LINK", "VOICE"
    val tags: String,
    val timestamp: Long,
    val url: String?,
    val description: String? = null,
    val imageUrl: String? = null,
    // Challenge Solution: Added the boolean field
    val isPinned: Boolean = false,
    val isPrivate: Boolean = false
)
