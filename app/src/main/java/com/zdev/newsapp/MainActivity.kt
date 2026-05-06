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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.FirebaseApp
import com.zdev.newsapp.ui.auth.MainNewsScreen
import com.zdev.newsapp.ui.auth.SettingsScreen
import com.zdev.newsapp.ui.auth.VerificationScreen
import com.zdev.newsapp.ui.news.DetailScreen
import com.zdev.newsapp.ui.theme.NewsAppTheme
import com.zdev.newsapp.ui.theme.ThemeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        setContent {
            // 2. Observe the theme state globally
            // val isDarkMode by mainViewModel.isDarkMode.collectAsState(initial = false)
            val themeViewModel: ThemeViewModel = viewModel()
            val isDarkMode by themeViewModel.isDarkMode.collectAsState()

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
                        startDestination = "main" // login
                    ) {
                        composable("login") {
                            LoginScreen(onLoginSuccess = {
                                navController.navigate("verification") {
                                    popUpTo("verification") { inclusive = true }
                                }
                            })
                        }

                        composable("verification") {
                            VerificationScreen(onVerificationSuccess = {
                                navController.navigate("main") {
                                    // Important: Clear the login and verification screens from history
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