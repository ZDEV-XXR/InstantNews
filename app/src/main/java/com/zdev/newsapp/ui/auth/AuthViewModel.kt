package com.zdev.newsapp.ui.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zdev.newsapp.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel() : ViewModel() {

    private val repository by lazy { AuthRepository() }
    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)

    fun loginUser(email: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                errorMessage.value = null
                val result = repository.login(email, pass)
                if (result.isSuccess) {
                    onSuccess()
                } else {
                    errorMessage.value = result.exceptionOrNull()?.message ?: "Login failed"
                }
            } catch (e: Exception) {
                errorMessage.value = e.localizedMessage
            } finally {
                isLoading.value = false // Guarantees loading stops
            }
        }
    }

    fun registerUser(email: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                errorMessage.value = null
                val result = repository.register(email, pass)
                if (result.isSuccess) {
                    onSuccess()
                } else {
                    errorMessage.value = result.exceptionOrNull()?.message ?: "Registration failed"
                }
            } catch (e: Exception) {
                errorMessage.value = e.localizedMessage
            } finally {
                isLoading.value = false
            }
        }
    }
}