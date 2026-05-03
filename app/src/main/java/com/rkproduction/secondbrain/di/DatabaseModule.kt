package com.rkproduction.secondbrain.di

import android.content.Context
import androidx.room.Room
import com.rkproduction.secondbrain.data.local.db.BrainDatabase
import com.rkproduction.secondbrain.data.local.db.BrainEntryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): BrainDatabase {
        return Room.databaseBuilder(
            context,
            BrainDatabase::class.java,
            "brain_database"
        )
        .addMigrations(
            BrainDatabase.MIGRATION_1_2, 
            BrainDatabase.MIGRATION_2_3,
            BrainDatabase.MIGRATION_3_4
        )
        .build()
    }

    @Provides
    fun provideBrainEntryDao(database: BrainDatabase): BrainEntryDao {
        return database.brainEntryDao
    }

    @Provides
    fun provideTemplateDao(database: BrainDatabase): com.rkproduction.secondbrain.data.local.db.TemplateDao {
        return database.templateDao
    }
}
