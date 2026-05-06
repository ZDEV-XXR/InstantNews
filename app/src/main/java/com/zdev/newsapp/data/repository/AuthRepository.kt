package com.zdev.newsapp.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthRepository(private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()) {

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

    // Check if the current user has verified their email
    fun isEmailVerified(): Boolean {
        return FirebaseAuth.getInstance().currentUser?.isEmailVerified ?: false
    }

    // Send the verification link to the user's inbox
    suspend fun sendVerificationEmail(): Result<Unit> {
        return try {
            // We get the current user (which was just created via register)
            val user = FirebaseAuth.getInstance().currentUser
            user?.sendEmailVerification()?.await()
            Result.success(Unit)
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

    fun logout() {
        FirebaseAuth.getInstance().signOut()
    }
}