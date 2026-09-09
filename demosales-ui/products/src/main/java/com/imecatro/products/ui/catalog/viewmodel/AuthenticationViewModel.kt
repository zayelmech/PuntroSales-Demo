package com.imecatro.products.ui.catalog.viewmodel

import androidx.lifecycle.viewModelScope
import com.imecatro.demosales.domain.core.auth.repository.AuthRepository
import com.imecatro.demosales.ui.theme.architect.BaseViewModel
import com.imecatro.products.ui.catalog.state.AuthenticationState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : BaseViewModel<AuthenticationState>(AuthenticationState.idle){

    override fun onStart() {
        viewModelScope.launch(Dispatchers.IO) {
            val isAuthenticated = authRepository.isUserAuthenticated()
            updateState { copy(isAuthenticated = isAuthenticated) }

            authRepository.getCurrentUserDisplayName()?.let { name ->
                updateState { copy(userName = name) }
            }
        }
    }

    fun signInWithGoogle(idToken: String) {

        viewModelScope.launch {
            updateState {
                copy(isAuthenticating = true, errorMessage = null)
            }

            authRepository.signInWithGoogle(idToken)
                .onSuccess { user ->
                    updateState {
                        copy(
                            userName = user.name,
                            isAuthenticating = false,
                            isAuthenticated = true
                        )
                    }
                }
                .onFailure { error ->
                    updateState {
                        copy(
                            isAuthenticating = false,
                            errorMessage = error.message ?: "Error al iniciar sesión"
                        )
                    }
                }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            updateState {
                copy(
                    isAuthenticated = false,
                    userName = ""
                )
            }
        }
    }
}