package com.zdev.newsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.FirebaseApp
import com.zdev.newsapp.ui.MainViewModel
import com.zdev.newsapp.ui.MainViewModelFactory
import com.zdev.newsapp.ui.auth.MainNewsScreen
import com.zdev.newsapp.ui.auth.SettingsScreen
import com.zdev.newsapp.ui.news.DetailScreen
import com.zdev.newsapp.ui.theme.NewsAppTheme
import com.zdev.newsapp.util.ThemeManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Initialize dependencies
        val themeManager = ThemeManager(this)
        val viewModelFactory = MainViewModelFactory(themeManager)
        val mainViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        FirebaseApp.initializeApp(this)

        setContent {
            // 2. Observe the theme state globally
            val isDarkMode by mainViewModel.isDarkMode.collectAsState(initial = false)

            NewsAppTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 3. Setup Navigation
                    val navController = rememberNavController()

                    // In MainActivity.kt
                    NavHost(
                        navController = navController,
                        startDestination = "login"
                    ) {
                        composable("login") {
                            LoginScreen(onLoginSuccess = {
                                navController.navigate("main") {
                                    popUpTo("login") { inclusive = true }
                                }
                            })
                        }

                        composable("main") {
                            MainNewsScreen(navController = navController)
                        }
                        composable("settings") {
                            SettingsScreen(navController = navController) // You'll need to create this simple screen
                        }

                        composable(
                            route = "detail/{url}",
                            arguments = listOf(navArgument("url") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val encodedUrl = backStackEntry.arguments?.getString("url") ?: ""
                            DetailScreen(encodedUrl)
                        }
                    }
                }
            }
        }
    }
}