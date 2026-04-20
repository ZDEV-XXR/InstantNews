package com.zdev.newsapp.ui.auth


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Settings") }) },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("main") },
                    label = { Text("Home") },
                    icon = { Icon(androidx.compose.material.icons.Icons.Default.Home, contentDescription = null) }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    label = { Text("Settings") },
                    icon = { Icon(androidx.compose.material.icons.Icons.Default.Settings, contentDescription = null) }
                )
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            Text("User Profile", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = { /* Add Logout Logic */ }) {
                Text("Logout")
            }
        }
    }
}