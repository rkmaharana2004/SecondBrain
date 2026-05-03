package com.rkproduction.secondbrain.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rkproduction.secondbrain.data.local.entity.BrainEntryEntity
import com.rkproduction.secondbrain.data.local.entity.BrainEntryFtsEntity
import com.rkproduction.secondbrain.data.local.entity.TemplateEntity

@Database(
    entities = [
        BrainEntryEntity::class, 
        BrainEntryFtsEntity::class,
        TemplateEntity::class
    ],
    version = 4, // Upgraded to support Metadata
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class BrainDatabase : RoomDatabase() {
    abstract val brainEntryDao : BrainEntryDao
    abstract val templateDao : TemplateDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE brain_entries ADD COLUMN isPrivate INTEGER NOT NULL DEFAULT 0")
            }
        }
        
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `templates` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `icon` TEXT NOT NULL, `fields` TEXT NOT NULL)")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE brain_entries ADD COLUMN description TEXT")
                db.execSQL("ALTER TABLE brain_entries ADD COLUMN imageUrl TEXT")
            }
        }
    }
}
