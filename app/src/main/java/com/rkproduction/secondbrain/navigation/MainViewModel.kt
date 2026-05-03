package com.rkproduction.secondbrain.navigation

import androidx.lifecycle.ViewModel
import com.rkproduction.secondbrain.util.AppLockManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val appLockManager: AppLockManager
) : ViewModel()
