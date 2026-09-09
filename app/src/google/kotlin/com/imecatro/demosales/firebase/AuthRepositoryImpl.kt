package com.imecatro.demosales.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.imecatro.demosales.domain.core.auth.repository.AuthRepository
import com.imecatro.demosales.domain.core.auth.repository.UserAuthenticated
import jakarta.inject.Inject
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override fun isUserAuthenticated(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override suspend fun signInWithGoogle(idToken: String): Result<UserAuthenticated> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(credential).await()
            val name = firebaseAuth.currentUser?.displayName ?: ""
            Result.success(UserAuthenticated(name))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }

    override fun getCurrentUserDisplayName(): String? {
        return firebaseAuth.currentUser?.displayName
    }
}
