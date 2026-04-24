package com.example.criminalalertprototype.api

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("v2/everything")
    suspend fun getCrimeNews(
        @Query("q") query: String,
        @Query("apiKey") apiKey: String,
        @Query("language") language: String = "en",
        @Query("pageSize") pageSize: Int = 20
    ): NewsResponse
}