package com.zdev.newsapp.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun VerificationScreen(
    onVerificationSuccess: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    // This effect runs automatically when the screen is shown
    LaunchedEffect(Unit) {
        while (true) {
            // Check status every 3 seconds
            viewModel.checkEmailVerificationStatus {
                onVerificationSuccess() // Navigate automatically!
            }
            kotlinx.coroutines.delay(3000)
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator() // Show a loader
            Spacer(modifier = Modifier.height(16.dp))
            Text("Waiting for email verification...")
            Text(
                "Click the link in your email to continue automatically.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}