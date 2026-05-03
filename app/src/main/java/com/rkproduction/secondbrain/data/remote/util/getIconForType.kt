package com.rkproduction.secondbrain.data.remote.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Mic
import androidx.compose.ui.graphics.vector.ImageVector
import com.rkproduction.secondbrain.domain.model.EntryType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
fun getIconForType(type: EntryType): ImageVector {
    return when (type) {
        EntryType.NOTE -> Icons.Default.EditNote
        EntryType.LINK -> Icons.Default.Link
        EntryType.VOICE -> Icons.Default.Mic
        EntryType.IMAGE -> Icons.Default.Image
    }
}

fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}