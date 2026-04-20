package com.zdev.newsapp.navigation

sealed class Screen(val route: String) {
    object Auth : Screen("auth_screen")
    object Main : Screen("main_screen")
    object Settings : Screen("settings_screen")
}