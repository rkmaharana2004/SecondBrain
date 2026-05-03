package com.rkproduction.secondbrain.presentation.tagcloud

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rkproduction.secondbrain.ui.components.TagCloudLayout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagCloudScreen(
    onNavigateBack: () -> Unit,
    viewModel: TagCloudViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tag Cloud") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(24.dp)) {
            TagCloudLayout {
                state.tags.forEach { tagCount ->
                    // Linear interpolation for font size: base 12sp + frequency-based boost
                    val fontSize = (12 + (tagCount.count * 2).coerceAtMost(24)).sp
                    
                    SuggestionChip(
                        onClick = { /* Filter home by tag later */ },
                        label = { Text(tagCount.tag, fontSize = fontSize) }
                    )
                }
            }
        }
    }
}
