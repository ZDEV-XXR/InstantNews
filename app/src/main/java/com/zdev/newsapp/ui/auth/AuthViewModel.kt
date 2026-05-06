package com.zdev.newsapp.ui.auth

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.zdev.newsapp.data.repository.AuthRepository
import com.zdev.newsapp.utils.NetworkUtils.isInternetAvailable
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel() : ViewModel() {

    private val repository by lazy { AuthRepository() }
    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)

    var isWaitingForVerification = mutableStateOf(false)

    fun loginUser(email: String, pass: String, context: Context, onSuccess: () -> Unit) {

        if (!isInternetAvailable(context)) {
            errorMessage.value = "Please check your internet connection."
            return
        }

        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            val result = repository.login(email, pass)

            if (result.isSuccess) {
                if (repository.isEmailVerified()) {
                    isLoading.value = false
                    onSuccess()
                } else {
                    // Block login and ask for verification
                    repository.sendVerificationEmail()
                    isLoading.value = false
                    errorMessage.value = "Email not verified. A new link has been sent to \$email. Please check your spam folder."
                    FirebaseAuth.getInstance().signOut()
                }
            } else {
                isLoading.value = false
                errorMessage.value = result.exceptionOrNull()?.message
            }
        }
    }

    fun checkEmailVerificationStatus(onVerified: () -> Unit) {
        viewModelScope.launch {
            // Refresh the user to get the latest status from Firebase servers
            val user = FirebaseAuth.getInstance().currentUser
            user?.reload()?.await()

            if (user?.isEmailVerified == true) {
                onVerified()
            }
        }
    }

    fun registerUser(email: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            val result = repository.register(email, pass)

            if (result.isSuccess) {

                val emailResult = repository.sendVerificationEmail()

                if (emailResult.isSuccess) {
                    isLoading.value = false
                    onSuccess() // This navigates to the VerificationScreen
                } else {
                    isLoading.value = false
                    errorMessage.value =
                        "Account created, but failed to send email: ${emailResult.exceptionOrNull()?.message}"
                }

            } else {

                isLoading.value = false
                errorMessage.value = result.exceptionOrNull()?.message
            }
        }
    }

    fun logout(onLogoutSuccess: () -> Unit) {
        repository.logout()
        onLogoutSuccess()
    }
}