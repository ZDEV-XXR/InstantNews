package com.zdev.newsapp.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthRepository(private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()) {

    // Check if a user is already logged in
    fun getCurrentUser(): FirebaseUser? = firebaseAuth.currentUser

    // Login logic
    suspend fun login(email: String, pass: String): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, pass).await()
            result.user?.let { Result.success(it) }
                ?: Result.failure(Exception("User not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Register logic
    suspend fun register(email: String, pass: String): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, pass).await()
            result.user?.let { Result.success(it) }
                ?: Result.failure(Exception("Registration failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() = firebaseAuth.signOut()
}