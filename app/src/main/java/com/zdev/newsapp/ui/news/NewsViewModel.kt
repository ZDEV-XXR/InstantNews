package com.zdev.newsapp.ui.news

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zdev.newsapp.data.model.Article
import com.zdev.newsapp.data.repository.NewsRepository
import kotlinx.coroutines.launch


class NewsViewModel : ViewModel() {
    private val repository = NewsRepository()

    private val _articles = mutableStateOf<List<Article>>(emptyList())
    val articles: State<List<Article>> = _articles

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    // Track the active category
    val categories = listOf("General", "Business", "Technology", "Entertainment", "Sports", "Science", "Health")
    private val _selectedCategory = mutableStateOf("General")
    val selectedCategory: State<String> = _selectedCategory

    init {
        fetchNews("general")
    }

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
        fetchNews(category.lowercase())
    }

    fun fetchNews(category: String = _selectedCategory.value.lowercase()) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getNewsArticles(category)
            _articles.value = if (result.isSuccess) result.getOrDefault(emptyList()) else emptyList()
            _isLoading.value = false
        }
    }
}