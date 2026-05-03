package com.rkproduction.secondbrain.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "security_prefs")

@Singleton
class AppLockManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val lastActiveKey = longPreferencesKey("last_active_timestamp")
    private val isLockEnabledKey = booleanPreferencesKey("is_lock_enabled")
    private val pinKey = stringPreferencesKey("app_pin")
    private val lockTimeoutMs = 2 * 60 * 1000L // 2 Minutes

    /**
     * Updates the timestamp of the user's last interaction.
     */
    suspend fun updateLastActive() {
        context.dataStore.edit { prefs ->
            prefs[lastActiveKey] = System.currentTimeMillis()
        }
    }

    /**
     * Checks if the app should be locked.
     */
    fun shouldLock(): Flow<Boolean> = context.dataStore.data.map { prefs ->
        val isEnabled = prefs[isLockEnabledKey] ?: false // Default to false if not set by user
        val pin = prefs[pinKey]
        
        // Only lock if enabled AND a PIN is actually set
        if (!isEnabled || pin == null) return@map false

        val lastActive = prefs[lastActiveKey] ?: 0L
        val currentTime = System.currentTimeMillis()
        
        lastActive == 0L || (currentTime - lastActive) > lockTimeoutMs
    }

    suspend fun lock() {
        context.dataStore.edit { prefs ->
            prefs[lastActiveKey] = 0L
        }
    }

    // Settings logic
    fun isLockEnabled(): Flow<Boolean> = context.dataStore.data.map { it[isLockEnabledKey] ?: false }
    
    suspend fun setLockEnabled(enabled: Boolean) {
        context.dataStore.edit { it[isLockEnabledKey] = enabled }
    }

    /**
     * Returns the PIN or null if not set.
     */
    fun getPin(): Flow<String?> = context.dataStore.data.map { it[pinKey] }

    suspend fun setPin(newPin: String) {
        context.dataStore.edit { it[pinKey] = newPin }
    }
}
