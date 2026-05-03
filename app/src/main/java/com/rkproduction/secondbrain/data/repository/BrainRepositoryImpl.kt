package com.rkproduction.secondbrain.data.repository

import com.rkproduction.secondbrain.data.local.db.BrainEntryDao
import com.rkproduction.secondbrain.data.local.entity.BrainEntryEntity
import com.rkproduction.secondbrain.domain.model.BrainEntry
import com.rkproduction.secondbrain.domain.model.EntryType
import com.rkproduction.secondbrain.domain.repository.BrainRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BrainRepositoryImpl @Inject constructor(private val dao: BrainEntryDao) : BrainRepository{
    override fun getAllEntries(): Flow<List<BrainEntry>> {
        return dao.getAllEntries().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun searchEntries(query: String): Flow<List<BrainEntry>> {
        return dao.searchEntries("*$query*").map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertEntry(entry: BrainEntry) {
        dao.insertEntry(entry.toEntity())
    }

    override suspend fun updateEntry(entry: BrainEntry) {
        dao.updateEntry(entry.toEntity())
    }

    override suspend fun deleteEntry(entry: BrainEntry) {
        dao.deleteEntry(entry.toEntity())
    }

    override fun getEntryById(entryId: Int): Flow<BrainEntry?> {
        return dao.getEntryById(entryId).map { it?.toDomain() }
    }

    override fun getPrivateEntries(): Flow<List<BrainEntry>> {
        return dao.getPrivateEntries().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun togglePrivacy(entryId: Int, isPrivate: Boolean) {
        dao.togglePrivacy(entryId, isPrivate)
    }

    override suspend fun getAllEntriesForBackup(): List<BrainEntry> {
        return dao.getAllEntriesForBackup().map { it.toDomain() }
    }

    // Mapper Helpers
    private fun BrainEntryEntity.toDomain() = BrainEntry(
        id = id,
        title = title,
        content = content,
        type = EntryType.valueOf(type),
        tags = if (tags.isBlank()) emptyList() else tags.split(","),
        timestamp = timestamp,
        url = url,
        description = description,
        imageUrl = imageUrl,
        isPinned = isPinned,
        isPrivate = isPrivate
    )

    private fun BrainEntry.toEntity() = BrainEntryEntity(
        id = id,
        title = title,
        content = content,
        type = type.name,
        tags = tags.joinToString(","),
        timestamp = timestamp,
        url = url,
        description = description,
        imageUrl = imageUrl,
        isPinned = isPinned,
        isPrivate = isPrivate
    )
}
