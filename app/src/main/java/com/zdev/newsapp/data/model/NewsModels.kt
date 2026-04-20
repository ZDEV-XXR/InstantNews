package com.zdev.newsapp.data.model

data class NewsResponse(
    val articles: List<Article>
)

data class Article(
    val title: String,
    val description: String?,
    val urlToImage: String?,
    val url: String,
    val source: Source
)

data class Source(
    val name: String
)