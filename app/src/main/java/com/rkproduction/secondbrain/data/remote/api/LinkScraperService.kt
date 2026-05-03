package com.rkproduction.secondbrain.data.remote.api

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Url

interface LinkScraperService {
    // 'suspend' tells Kotlin this function requires Coroutines and takes time to finish
    @GET
    suspend fun fetchHtml(@Url url: String): ResponseBody
}