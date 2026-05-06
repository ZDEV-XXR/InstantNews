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

    private var currentPage = 1
    private val _isLastPage = mutableStateOf(false)
    val isLastPage: State<Boolean> = _isLastPage


    private val _isSearching = mutableStateOf(false)
    val isSearching: State<Boolean> = _isSearching

    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery

    init {
        fetchNews("general")
    }

    fun toggleSearchMode() {
        _isSearching.value = !_isSearching.value
        if (!_isSearching.value) {
            _searchQuery.value = ""
            fetchNews(isNewRequest = true) // Reset to normal news
        }
    }

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onCategoryAtIndex(index: Int) {
        val category = categories[index]
        if (category != _selectedCategory.value) {
            onCategorySelected(category)
        }
    }

    // In NewsViewModel.kt
    fun fetchNews(category: String = _selectedCategory.value.lowercase(), isNewRequest: Boolean = false) {
        if (isNewRequest) {
            currentPage = 1
            _isLastPage.value = false // Reset for new requests
            _articles.value = emptyList()
        }

        if (_isLastPage.value || _isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getNewsArticles(category, page = currentPage)

            if (result.isSuccess) {
                val newArticles = result.getOrNull() ?: emptyList()
                if (newArticles.isEmpty()) {
                    _isLastPage.value = true // Mark as end of content
                } else {
                    _articles.value += newArticles
                    currentPage++
                }
            }
            _isLoading.value = false
        }
    }
    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
        fetchNews(category.lowercase(), isNewRequest = true)
    }

    fun performSearch(query: String, isNewRequest: Boolean = true) {
        if (query.isEmpty()) return

        if (isNewRequest) {
            currentPage = 1
            _isLastPage.value = false
            _articles.value = emptyList()
        }

        // Stop if we are already loading or at the end
        if (_isLastPage.value || _isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            _isSearching.value = true

            // Request the CURRENT page
            val result = repository.searchNews(query, page = currentPage)

            if (result.isSuccess) {
                val newArticles = result.getOrNull() ?: emptyList()
                if (newArticles.isEmpty()) {
                    _isLastPage.value = true
                } else {
                    // ADD the new articles to the existing list instead of replacing
                    _articles.value += newArticles
                    // INCREMENT for the next scroll trigger
                    currentPage++
                }
            }
            _isLoading.value = false
        }
    }

    fun closeSearch() {
        _isSearching.value = false
        _searchQuery.value = ""
        fetchNews(isNewRequest = true) // Return to headlines
    }
}
