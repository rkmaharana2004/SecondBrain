package com.rkproduction.secondbrain.receiver

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.rkproduction.secondbrain.data.remote.util.LinkMetadataFetcher
import com.rkproduction.secondbrain.domain.model.BrainEntry
import com.rkproduction.secondbrain.domain.model.EntryType
import com.rkproduction.secondbrain.domain.repository.BrainRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class ShareActivity : ComponentActivity() {

    @Inject lateinit var repository: BrainRepository
    @Inject lateinit var linkFetcher: LinkMetadataFetcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Check if the envelope is the correct type
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            // Extract the text (which could be a URL or just highlighted text)
            val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)

            if (sharedText != null) {
                saveIncomingData(sharedText)
            } else {
                finish() // No text found, abort silently
            }
        } else {
            finish() // Wrong intent type, abort silently
        }
    }

    private fun saveIncomingData(text: String) {
        // 2. Launch on the IO thread so we don't freeze the screen while fetching OpenGraph data
        lifecycleScope.launch(Dispatchers.IO) {

            val isLink = text.startsWith("http")
            
            val (title, description, imageUrl) = if (isLink) {
                val meta = linkFetcher.fetchMetadata(text)
                Triple(meta.title ?: "Saved Link", meta.description, meta.imageUrl)
            } else {
                Triple(text.take(30) + "...", null, null)
            }

            val entry = BrainEntry(
                title = title,
                content = text,
                type = if (isLink) EntryType.LINK else EntryType.NOTE,
                tags = emptyList(),
                timestamp = System.currentTimeMillis(),
                url = if (isLink) text else null,
                description = description,
                imageUrl = imageUrl
            )

            // 3. Save to database
            repository.insertEntry(entry)

            // 4. Switch back to Main thread to show the Toast UI, then close the Ghost Activity
            withContext(Dispatchers.Main) {
                Toast.makeText(this@ShareActivity, "Saved to Brain 🧠", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
