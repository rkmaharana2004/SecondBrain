package com.rkproduction.secondbrain.di

import com.rkproduction.secondbrain.data.repository.BrainRepositoryImpl
import com.rkproduction.secondbrain.domain.repository.BrainRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindBrainRepository(
        brainRepositoryImpl: BrainRepositoryImpl
    ): BrainRepository
}
