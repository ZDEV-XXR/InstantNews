package com.zdev.newsapp.ui.news


import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(encodedUrl: String) {
    val url = remember(encodedUrl) {
        try {
            URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8.toString())
        } catch (e: Exception) {
            ""
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Article") })
        }
    ) { padding ->
        // Check the URL in a proper Composable context
        if (url.isEmpty() || !url.startsWith("http")) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Error: Invalid or missing URL")
            }
        } else {
            // AndroidView is a Composable, so it's fine here
            AndroidView(
                modifier = Modifier.padding(padding),
                factory = { context ->
                    WebView(context).apply {
                        webViewClient = WebViewClient()
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true // Fixes most "white screen" issues
                            mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            userAgentString = "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Mobile Safari/537.36"
                        }
                        loadUrl(url)
                    }
                }
            )
        }
    }
}