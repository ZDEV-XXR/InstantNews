package com.zdev.newsapp.data.repository

import com.zdev.newsapp.data.model.Article
import com.zdev.newsapp.data.remote.RetrofitClient

class NewsRepository {
    private val api = RetrofitClient.newsApi

    // Add 'category' as a parameter here
    suspend fun getNewsArticles(category: String): Result<List<Article>> {
        return try {
            val response = api.getTopHeadlines(category = category)
            Result.success(response.articles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}