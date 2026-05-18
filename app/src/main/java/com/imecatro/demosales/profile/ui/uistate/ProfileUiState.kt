package com.imecatro.demosales.profile.ui.uistate

import com.imecatro.demosales.profile.domain.model.UserProfileDomainModel
import com.imecatro.demosales.ui.theme.architect.ErrorUiModel
import com.imecatro.demosales.ui.theme.architect.Idle
import com.imecatro.demosales.ui.theme.architect.UiState

data class ProfileUiState(
    val profile: UserProfileDomainModel,
    val isLoading: Boolean = false,
    val profileError: String? = null
) : UiState {
    override fun isFetchingOrProcessingData(): Boolean = isLoading

    override fun getError(): ErrorUiModel? = profileError?.let { ErrorUiModel(message = it) }

    companion object : Idle<ProfileUiState> {
        override val idle: ProfileUiState
            get() = ProfileUiState(
                profile = UserProfileDomainModel.default
            )
    }
}
