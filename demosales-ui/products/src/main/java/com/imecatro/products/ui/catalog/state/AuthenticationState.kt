package com.imecatro.products.ui.catalog.state

import com.imecatro.demosales.ui.theme.architect.Idle

data class AuthenticationState(
    val isAuthenticating: Boolean = false,
    val isAuthenticated: Boolean = false,
    val userName: String = "",
    val errorMessage: String? = null,
) {
    companion object : Idle<AuthenticationState> {
        override val idle: AuthenticationState
            get() = AuthenticationState()

    }
}