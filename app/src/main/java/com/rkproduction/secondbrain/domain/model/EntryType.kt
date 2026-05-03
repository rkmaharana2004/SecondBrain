package com.rkproduction.secondbrain.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class EntryType {
    NOTE,
    LINK,
    VOICE,
    IMAGE
}