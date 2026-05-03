package com.rkproduction.secondbrain.domain.repository

import com.rkproduction.secondbrain.domain.model.BrainEntry
import kotlinx.coroutines.flow.Flow

interface BrainRepository {
    fun getAllEntries(): Flow<List<BrainEntry>>
    // We will add insert/search here in later phases
    // Add to interface
    fun searchEntries(query: String): Flow<List<BrainEntry>>
    suspend fun insertEntry(entry: BrainEntry)
    suspend fun updateEntry(entry: BrainEntry)
    suspend fun deleteEntry(entry: BrainEntry)
    fun getEntryById(entryId: Int): Flow<BrainEntry?>
    
    // Vault & Backup additions
    fun getPrivateEntries(): Flow<List<BrainEntry>>
    suspend fun togglePrivacy(entryId: Int, isPrivate: Boolean)
    suspend fun getAllEntriesForBackup(): List<BrainEntry>
}