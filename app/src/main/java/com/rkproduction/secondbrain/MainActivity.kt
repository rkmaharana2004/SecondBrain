package com.rkproduction.secondbrain

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.rkproduction.secondbrain.navigation.NavGraph
import com.rkproduction.secondbrain.ui.theme.SecondBrainTheme
import com.rkproduction.secondbrain.util.AppLockManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject lateinit var appLockManager: AppLockManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🕵️ Observe the App's lifecycle (Background/Foreground)
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStop(owner: LifecycleOwner) {
                // App went to background - record the time
                lifecycleScope.launch {
                    appLockManager.updateLastActive()
                }
            }
        })

        enableEdgeToEdge()
        setContent {
            SecondBrainTheme {
                NavGraph()
            }
        }
    }
}


