package com.rkproduction.secondbrain.data.local.entity

import androidx.room.Entity
import androidx.room.Fts4

@Fts4(contentEntity = BrainEntryEntity::class)
@Entity(tableName = "brain_entries_fts")
data class BrainEntryFtsEntity(
    val title: String,
    val content: String,
    val tags: String
)
