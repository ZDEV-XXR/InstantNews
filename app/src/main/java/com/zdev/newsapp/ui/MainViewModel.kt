package com.zdev.newsapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow
import com.zdev.newsapp.util.ThemeManager

class MainViewModel(private val themeManager: ThemeManager) : ViewModel() {

    // Expose the theme as a Flow for the UI to collect
    val isDarkMode: Flow<Boolean> = themeManager.isDarkMode

    fun toggleTheme(isDark: Boolean) {
        viewModelScope.launch {
            themeManager.toggleTheme(isDark)
        }
    }
}

// The Factory tells Android how to build the ViewModel with the ThemeManager dependency
class MainViewModelFactory(private val themeManager: ThemeManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(themeManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}