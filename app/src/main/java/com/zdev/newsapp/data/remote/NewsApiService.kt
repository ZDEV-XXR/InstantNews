package com.zdev.newsapp.data.remote

import com.zdev.newsapp.data.model.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query


// API Key: 389756a5ad3147e6a533faae76fb3772
interface NewsApiService {
    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String = "us",
        @Query("category") category: String,
        @Query("apiKey") apiKey: String = "389756a5ad3147e6a533faae76fb3772"
    ): NewsResponse
}