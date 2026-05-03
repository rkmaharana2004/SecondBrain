package com.rkproduction.secondbrain.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class BrainEntry(
    val id: Int = 0,
    val title: String,
    val content: String,
    val type: EntryType, // Upgraded to Enum for type safety
    val tags: List<String>, // Upgraded to List so Compose can iterate over it
    val timestamp: Long,
    val url: String?,
    val description: String? = null,
    val imageUrl: String? = null,
    val isPinned: Boolean = false, // Your challenge addition fits perfectly here
    val isPrivate: Boolean = false
)
