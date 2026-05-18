package com.imecatro.demosales.ui.uistate

import com.imecatro.demosales.ui.theme.architect.ErrorUiModel
import com.imecatro.demosales.ui.theme.architect.Idle
import com.imecatro.demosales.ui.theme.architect.UiState

data class MainUiState(
    val isDarkTheme: Boolean? = null,
    val isLoading: Boolean = false
) : UiState {
    override fun isFetchingOrProcessingData(): Boolean = isLoading

    override fun getError(): ErrorUiModel? = null

    companion object : Idle<MainUiState> {
        override val idle: MainUiState
            get() = MainUiState()
    }
}
