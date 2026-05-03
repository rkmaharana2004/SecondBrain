package com.rkproduction.secondbrain.data.local.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rkproduction.secondbrain.data.local.entity.BrainEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BrainEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: BrainEntryEntity): Long

    @Update
    suspend fun updateEntry(entry: BrainEntryEntity)

    @Delete
    suspend fun deleteEntry(entry: BrainEntryEntity)

    @Query("SELECT * FROM brain_entries WHERE isPrivate = 0 ORDER BY timestamp DESC")
    fun getAllEntries(): Flow<List<BrainEntryEntity>>

    @Query("SELECT * FROM brain_entries WHERE isPinned = 1 AND isPrivate = 0 ORDER BY timestamp DESC")
    fun getPinnedEntries(): Flow<List<BrainEntryEntity>>

    @Query("SELECT * FROM brain_entries WHERE isPrivate = 1 ORDER BY timestamp DESC")
    fun getPrivateEntries(): Flow<List<BrainEntryEntity>>

    @Query("UPDATE brain_entries SET isPrivate = :isPrivate WHERE id = :entryId")
    suspend fun togglePrivacy(entryId: Int, isPrivate: Boolean)

    @Query("""
        SELECT * FROM brain_entries 
        JOIN brain_entries_fts ON brain_entries.id = brain_entries_fts.docid 
        WHERE brain_entries_fts MATCH :searchQuery AND isPrivate = 0
    """)
    fun searchEntries(searchQuery: String): Flow<List<BrainEntryEntity>>

    @Query("SELECT * FROM brain_entries")
    suspend fun getAllEntriesForBackup(): List<BrainEntryEntity>

    @Query("SELECT * FROM brain_entries WHERE id = :entryId")
    fun getEntryById(entryId: Int): Flow<BrainEntryEntity?>

    // --- Stats & Insights (Feature 7, 8, 9) ---

    @Query("SELECT type, COUNT(*) as count FROM brain_entries GROUP BY type")
    suspend fun getEntryCountByType(): List<TypeCount>

    @Query("SELECT tags FROM brain_entries")
    suspend fun getAllTagsRaw(): List<String>

    @Query("SELECT timestamp FROM brain_entries ORDER BY timestamp ASC")
    suspend fun getAllTimestamps(): List<Long>
}

data class TypeCount(val type: String, val count: Int)
