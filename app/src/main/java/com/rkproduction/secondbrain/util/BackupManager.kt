package com.rkproduction.secondbrain.util

import android.content.Context
import android.net.Uri
import com.rkproduction.secondbrain.domain.model.BrainEntry
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

class BackupManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val json = Json { 
        prettyPrint = true
        ignoreUnknownKeys = true 
    }

    suspend fun exportToJson(uri: Uri, entries: List<BrainEntry>): Boolean = withContext(Dispatchers.IO) {
        try {
            val content = json.encodeToString(entries)
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(content.toByteArray())
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun importFromJson(uri: Uri): List<BrainEntry>? = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val reader = BufferedReader(InputStreamReader(inputStream))
                val content = reader.readText()
                json.decodeFromString<List<BrainEntry>>(content)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
