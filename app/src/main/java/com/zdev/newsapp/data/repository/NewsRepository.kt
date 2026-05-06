package com.zdev.newsapp.data.repository

import com.zdev.newsapp.data.model.Article
import com.zdev.newsapp.data.remote.RetrofitClient
import retrofit2.http.GET
import retrofit2.http.Headers

class NewsRepository {
    private val api = RetrofitClient.newsApi

    // Add 'category' as a parameter here
    @Headers("X-No-Cache: true")
    @GET("v2/top-headlines")
    suspend fun getNewsArticles(category: String, page: Int = 1): Result<List<Article>> {
        return try {
            // PASS 'page' to the API here
            val response = api.getTopHeadlines(category = category, page = page)
            Result.success(response.articles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchNews(query: String, page: Int = 1): Result<List<Article>> {
        return try {
            // Ensure 'page' is being passed to the API interface
            val response = api.searchNews(query = query, page = page)
            Result.success(response.articles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}