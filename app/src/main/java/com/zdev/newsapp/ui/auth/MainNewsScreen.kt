package com.zdev.newsapp.ui.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.zdev.newsapp.data.model.Article
import com.zdev.newsapp.ui.news.NewsViewModel
import kotlinx.coroutines.launch
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
    val isSearching by viewModel.isSearching
    val searchQuery by viewModel.searchQuery
    val isLastPage by viewModel.isLastPage
    val categories = viewModel.categories

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { categories.size })

    // Sync Pager swipes with Category selection
    LaunchedEffect(pagerState.currentPage) {
        if (!isSearching) {
            viewModel.onCategoryAtIndex(pagerState.currentPage)
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        if (isSearching) {
                            TextField(
                                value = searchQuery,
                                onValueChange = { viewModel.onSearchQueryChanged(it) },
                                placeholder = { Text("Search news...") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(
                                    onSearch = { viewModel.performSearch(searchQuery) }
                                ),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
                                )
                            )
                        } else {
                            Text("Daily News", fontWeight = FontWeight.Bold)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            if (isSearching) viewModel.closeSearch() else viewModel.toggleSearchMode()
                        }) {
                            Icon(
                                imageVector = if (isSearching) Icons.Default.Close else Icons.Default.Search,
                                contentDescription = "Search"
                            )
                        }
                    }
                )

                if (!isSearching) {
                    ScrollableTabRow(
                        selectedTabIndex = pagerState.currentPage,
                        edgePadding = 16.dp
                    ) {
                        categories.forEachIndexed { index, category ->
                            Tab(
                                selected = (pagerState.currentPage == index),
                                onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                                text = { Text(category) }
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = !isSearching,
                    onClick = { if (isSearching) viewModel.closeSearch() },
                    label = { Text("Home") },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("settings") },
                    label = { Text("Settings") },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) }
                )
            }
        }
    ) { padding ->
        // FIX: Switch UI between Search list and Category Pager
        if (isSearching) {
            val searchScrollState = rememberLazyListState()

            // Search Pagination
            val shouldLoadMore = remember {
                derivedStateOf {
                    val lastVisibleItem = searchScrollState.layoutInfo.visibleItemsInfo.lastOrNull()
                    // Use 'index' safely here
                    lastVisibleItem != null && lastVisibleItem.index >= articles.size - 3
                }
            }

            LaunchedEffect(shouldLoadMore.value) {
                if (shouldLoadMore.value && !isLoading && !isLastPage) {
                    viewModel.performSearch(searchQuery, isNewRequest = false)
                }
            }

            PullToRefreshBox(
                isRefreshing = isLoading,
                onRefresh = { viewModel.performSearch(searchQuery) },
                modifier = Modifier.padding(padding)
            ) {
                    // Pass the searchScrollState to the NewsList
                NewsList(articles, isLoading, isLastPage, searchScrollState, navController)
            }

        } else {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.padding(padding).fillMaxSize()
            ) { pageIndex ->
                val categoryScrollState = rememberLazyListState()
                val category = categories[pageIndex]

                val shouldLoadMore = remember {
                    derivedStateOf {
                        val lastVisibleItem = categoryScrollState.layoutInfo.visibleItemsInfo.lastOrNull()
                        lastVisibleItem != null && lastVisibleItem.index >= articles.size - 3
                    }
                }

                LaunchedEffect(shouldLoadMore.value) {
                    if (shouldLoadMore.value && !isLoading && !isLastPage) {
                        viewModel.fetchNews(category = category.lowercase())
                    }
                }

                PullToRefreshBox(
                    isRefreshing = isLoading,
                    onRefresh = { viewModel.fetchNews(isNewRequest = true) }
                ) {
                    // Pass the categoryScrollState to the NewsList
                    NewsList(articles, isLoading, isLastPage, categoryScrollState, navController)
                }
            }
        }
    }
}

@Composable
fun NewsList(
    articles: List<Article>,
    isLoading: Boolean,
    isLastPage: Boolean,
    scrollState: androidx.compose.foundation.lazy.LazyListState,
    navController: NavController
) {
    LazyColumn(modifier = Modifier.fillMaxSize(), state = scrollState) {
        items(articles) { article ->
            NewsItem(article = article) {
                val encodedUrl = URLEncoder.encode(article.url, StandardCharsets.UTF_8.toString())
                navController.navigate("detail/$encodedUrl")
            }
        }

        if (isLoading && articles.isNotEmpty()) {
            item {
                CircularProgressIndicator(
                    modifier = Modifier.fillMaxWidth().padding(16.dp).wrapContentWidth(Alignment.CenterHorizontally)
                )
            }
        }

        if (isLastPage && articles.isNotEmpty()) {
            item {
                Text(
                    text = "You've caught up! You have the latest news.",
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
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
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .build(),
                contentDescription = "Image",
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