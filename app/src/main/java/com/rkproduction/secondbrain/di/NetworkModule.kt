package com.rkproduction.secondbrain.di

import com.rkproduction.secondbrain.data.remote.api.LinkScraperService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideLinkScraper(okHttpClient: OkHttpClient): LinkScraperService {
        return Retrofit.Builder()
            .baseUrl("https://dummy.baseurl/")
            .client(okHttpClient)
            .build()
            .create(LinkScraperService::class.java)
    }
}