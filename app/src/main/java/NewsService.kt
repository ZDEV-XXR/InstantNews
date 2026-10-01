package com.zdev.newsapp.data.services

import com.zdev.newsapp.BuildConfig
import com.zdev.newsapp.data.model.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

val API = BuildConfig.NEWS_API

interface NewsApiService {
    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String = "us",
        @Query("category") category: String,
        @Query("apiKey") apiKey: String = API,
        @Query("page") page: Int
    ): NewsResponse

    @GET("v2/everything")
    suspend fun searchNews(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("apiKey") apiKey: String = API
    ): NewsResponse
}
