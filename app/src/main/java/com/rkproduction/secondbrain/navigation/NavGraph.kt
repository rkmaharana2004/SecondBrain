package com.rkproduction.secondbrain.navigation

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rkproduction.secondbrain.presentation.home.HomeScreen
import com.rkproduction.secondbrain.presentation.search.SearchScreen
import com.rkproduction.secondbrain.presentation.capture.CaptureBottomSheet
import com.rkproduction.secondbrain.presentation.lock.LockScreen
import com.rkproduction.secondbrain.presentation.vault.VaultScreen
import com.rkproduction.secondbrain.presentation.dashboard.DashboardScreen
import com.rkproduction.secondbrain.presentation.streak.StreakScreen
import com.rkproduction.secondbrain.presentation.tagcloud.TagCloudScreen
import com.rkproduction.secondbrain.presentation.settings.SettingsScreen
import com.rkproduction.secondbrain.presentation.detail.EntryDetailScreen
import com.rkproduction.secondbrain.presentation.onboarding.OnboardingScreen
import com.rkproduction.secondbrain.util.AppLockManager
import kotlinx.coroutines.flow.first

@Composable
fun NavGraph(appLockManager: AppLockManager = hiltViewModel<MainViewModel>().appLockManager) {
    val navController = rememberNavController()
    var showCaptureSheet by remember { mutableStateOf(false) }
    var entryToEdit by remember { mutableStateOf<com.rkproduction.secondbrain.domain.model.BrainEntry?>(null) }
    
    val shouldLock by appLockManager.shouldLock().collectAsStateWithLifecycle(initialValue = false)

    LaunchedEffect(Unit) {
        val pin = appLockManager.getPin().first()
        if (pin == null) {
            navController.navigate("onboarding") {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    LaunchedEffect(shouldLock) {
        if (shouldLock) {
            navController.navigate("lock") { popUpTo(0) { inclusive = true } }
        }
    }

    NavHost(navController = navController, startDestination = "home") {
        composable("onboarding") {
            OnboardingScreen(
                onComplete = { navController.navigate("home") { popUpTo("onboarding") { inclusive = true } } },
                onSkip = { navController.navigate("home") { popUpTo("onboarding") { inclusive = true } } }
            )
        }

        composable("lock") {
            LockScreen(onAuthenticated = { navController.navigate("home") { popUpTo("lock") { inclusive = true } } })
        }

        composable("home") {
            HomeScreen(
                onNavigateToCapture = { 
                    entryToEdit = null
                    showCaptureSheet = true 
                },
                onNavigateToSearch = { navController.navigate("search") },
                onNavigateToDashboard = { navController.navigate("dashboard") },
                onNavigateToStreak = { navController.navigate("streak") },
                onNavigateToTags = { navController.navigate("tags") },
                onNavigateToSettings = { navController.navigate("settings") },
                onNavigateToEdit = { entry ->
                    entryToEdit = entry
                    showCaptureSheet = true
                },
                onNavigateToDetail = { entryId ->
                    navController.navigate("detail/$entryId")
                }
            )
        }

        composable("detail/{entryId}", 
            arguments = listOf(androidx.navigation.navArgument("entryId") { type = androidx.navigation.NavType.IntType })
        ) {
            EntryDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { entry ->
                    entryToEdit = entry
                    showCaptureSheet = true
                }
            )
        }

        composable("vault") { VaultScreen(onNavigateBack = { navController.popBackStack() }) }
        composable("search") { SearchScreen(onNavigateBack = { navController.popBackStack() }) }
        composable("dashboard") { DashboardScreen(onNavigateBack = { navController.popBackStack() }) }
        composable("streak") { StreakScreen(onNavigateBack = { navController.popBackStack() }) }
        composable("tags") { TagCloudScreen(onNavigateBack = { navController.popBackStack() }) }
        composable("settings") { 
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToVault = { navController.navigate("vault") }
            ) 
        }
    }

    if (showCaptureSheet) {
        CaptureBottomSheet(
            onDismiss = { 
                showCaptureSheet = false 
                entryToEdit = null
            },
            entryToEdit = entryToEdit
        )
    }
}
