package com.zdev.newsapp

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zdev.newsapp.ui.auth.AuthViewModel
import com.zdev.newsapp.utils.NetworkUtils.isInternetAvailable


// Testing Mail : zdevapp@protonmail.com PASS : 098765
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = viewModel() // Use the ViewModel here
) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val isRegisterMode = remember { mutableStateOf(false) }

    val loading by viewModel.isLoading
    val error by viewModel.errorMessage

    val context = LocalContext.current

    // Inside LoginScreen.kt Column
    if (viewModel.isWaitingForVerification.value) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Verify your Email",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "We sent a code to your inbox. Please click the link, then try logging in again.",
                    style = MaterialTheme.typography.bodySmall
                )
                Button(
                    onClick = { viewModel.isWaitingForVerification.value = false },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Got it")
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 1. App Logo / Icon
            Icon(
                imageVector = Icons.Default.Newspaper,
                contentDescription = "Logo",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Title & Subtitle
            Text(
                text = if (isRegisterMode.value) "Join the Feed" else "Welcome",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = if (isRegisterMode.value) "Create an account to save news" else "Email : zdevapp@protonmail.com || Pass : 098765",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 3. Error Message (if any)
            if (error != null) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(8.dp),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 4. Input Fields
            OutlinedTextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done)
            )

            Spacer(modifier = Modifier.height(30.dp))

            // 5. Main Action Button
            Button(
                onClick = {
                    if (isInternetAvailable(context)) {
                        if (isRegisterMode.value) {
                            viewModel.registerUser(email.value, password.value, onLoginSuccess)
                        } else {
                            viewModel.loginUser(email.value, password.value, context, onLoginSuccess)
                        }
                    } else {
                        // Show a Toast or update an error state in the ViewModel
                        Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !loading
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp)
                } else {
                    Text(
                        text = if (isRegisterMode.value) "Create Account" else "Sign In",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            /*
            // 6. Switch Mode Button
            TextButton(
                onClick = {
                    isRegisterMode.value = !isRegisterMode.value
                    viewModel.errorMessage.value = null // Clear errors when switching
                }
            ) {
                Text(
                    text = if (isRegisterMode.value) "Have an account? Sign In" else "New here? Create Account",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            */
        }
    }
}