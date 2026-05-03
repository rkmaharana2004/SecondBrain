package com.rkproduction.secondbrain.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rkproduction.secondbrain.ui.components.EntryCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToCapture: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToStreak: () -> Unit,
    onNavigateToTags: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToEdit: (com.rkproduction.secondbrain.domain.model.BrainEntry) -> Unit,
    onNavigateToDetail: (Int) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    
    val isExpanded by remember {
        derivedStateOf { scrollBehavior.state.collapsedFraction < 0.5f }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                )
            )
    ) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            containerColor = Color.Transparent,
            topBar = {
                LargeTopAppBar(
                    title = { Text("Brain", style = MaterialTheme.typography.headlineLarge) },
                    actions = {
                        IconButton(onClick = onNavigateToDashboard) { Icon(Icons.Default.BarChart, contentDescription = "Stats") }
                        IconButton(onClick = onNavigateToStreak) { Icon(Icons.Default.LocalFireDepartment, contentDescription = "Streak", tint = Color(0xFFFF5722)) }
                        IconButton(onClick = onNavigateToTags) { Icon(Icons.Default.Tag, contentDescription = "Tags") }
                        IconButton(onClick = onNavigateToSearch) { Icon(Icons.Default.Search, contentDescription = "Search") }
                        IconButton(onClick = onNavigateToSettings) { Icon(Icons.Default.Settings, contentDescription = "Settings") }
                    },
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.9f)
                    )
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = onNavigateToCapture,
                    expanded = isExpanded,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("CAPTURE") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                    elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp)
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
                when (val currentState = state) {
                    is HomeUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    is HomeUiState.Error -> Text("ERROR", modifier = Modifier.align(Alignment.Center))
                    is HomeUiState.Success -> {
                        if (currentState.entries.isEmpty()) {
                            EmptyState(modifier = Modifier.align(Alignment.Center))
                        } else {
                            val (pinned, unpinned) = remember(currentState.entries) {
                                currentState.entries.partition { it.isPinned }
                            }
                            LazyColumn(
                                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 100.dp),
                                verticalArrangement = Arrangement.spacedBy(20.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                if (pinned.isNotEmpty()) {
                                    item { SectionHeader("PINNED") }
                                    items(pinned, key = { it.id }) { entry ->
                                        EntryCard(
                                            entry = entry,
                                            onClick = { onNavigateToDetail(entry.id) },
                                            onDelete = { viewModel.deleteEntry(entry) },
                                            onEdit = { onNavigateToEdit(entry) },
                                            onTogglePrivacy = { viewModel.togglePrivacy(entry) }
                                        )
                                    }
                                }

                                if (unpinned.isNotEmpty()) {
                                    item {
                                        SectionHeader(if (pinned.isNotEmpty()) "LATEST" else "KNOWLEDGE")
                                    }
                                    items(unpinned, key = { it.id }) { entry ->
                                        EntryCard(
                                            entry = entry,
                                            onClick = { onNavigateToDetail(entry.id) },
                                            onDelete = { viewModel.deleteEntry(entry) },
                                            onEdit = { onNavigateToEdit(entry) },
                                            onTogglePrivacy = { viewModel.togglePrivacy(entry) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
        modifier = Modifier.padding(start = 4.dp)
    )
}

@Composable
fun EmptyState(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(48.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Your brain is waiting.", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Capture a thought to begin.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.outline, textAlign = TextAlign.Center)
    }
}
