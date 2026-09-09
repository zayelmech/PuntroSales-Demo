package com.imecatro.demosales.domain.core.auth.repository


interface AuthRepository {
    fun isUserAuthenticated(): Boolean
    suspend fun signInWithGoogle(idToken: String): Result<UserAuthenticated>
    suspend fun signOut()
    fun getCurrentUserDisplayName(): String?
}


data class UserAuthenticated(val name: String)