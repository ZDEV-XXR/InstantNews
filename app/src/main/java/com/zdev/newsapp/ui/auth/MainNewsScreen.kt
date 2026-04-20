package com.zdev.newsapp.ui.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.zdev.newsapp.data.model.Article
import com.zdev.newsapp.ui.news.NewsViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNewsScreen(
    navController: NavController,
    viewModel: NewsViewModel = viewModel()
) {
    val articles by viewModel.articles
    val isLoading by viewModel.isLoading
    val selectedCategory by viewModel.selectedCategory

    Scaffold(
        topBar = {
            Column {
                TopAppBar(title = { Text("Daily News", fontWeight = FontWeight.Bold) })
                ScrollableTabRow(
                    selectedTabIndex = viewModel.categories.indexOf(selectedCategory),
                    edgePadding = 16.dp
                ) {
                    viewModel.categories.forEach { category ->
                        Tab(
                            selected = (selectedCategory == category),
                            onClick = { viewModel.onCategorySelected(category) },
                            text = { Text(category) }
                        )
                    }
                }
            }
        },

        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true, // We are on the Main screen
                    onClick = { /* Already here */ },
                    label = { Text("Home") },
                    icon = { Icon(androidx.compose.material.icons.Icons.Default.Home, contentDescription = null) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("settings") },
                    label = { Text("Settings") },
                    icon = { Icon(androidx.compose.material.icons.Icons.Default.Settings, contentDescription = null) }
                )
            }
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = { viewModel.fetchNews() },
            modifier = Modifier.padding(padding)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(articles) { article ->
                    NewsItem(article = article) {
                        val encodedUrl = URLEncoder.encode(article.url, StandardCharsets.UTF_8.toString())
                        navController.navigate("detail/$encodedUrl")
                    }
                }
            }
        }
    }
}

@Composable
fun NewsItem(article: Article, onClick: () -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable{ onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {

        Column {
            // News Image
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(article.urlToImage)
                    .crossfade(true)             // Optional: add a local drawable
                    .build(),
                contentDescription = "News Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )

            // Text Content
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = article.description ?: "No description available",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 3
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Source: ${article.source.name}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}