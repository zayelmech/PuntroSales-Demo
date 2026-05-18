package com.imecatro.demosales.profile.ui.viewmodels

import androidx.lifecycle.viewModelScope
import com.imecatro.demosales.domain.core.architecture.usecase.onAny
import com.imecatro.demosales.profile.domain.usecases.GetProfileUseCase
import com.imecatro.demosales.profile.domain.usecases.UpdateProfileUseCase
import com.imecatro.demosales.profile.ui.mappers.toDomainModel
import com.imecatro.demosales.profile.ui.mappers.toUiModel
import com.imecatro.demosales.profile.ui.model.UserProfileUiModel
import com.imecatro.demosales.profile.ui.uistate.ProfileUiState
import com.imecatro.demosales.ui.theme.architect.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase
) : BaseViewModel<ProfileUiState>(ProfileUiState.idle) {

    override fun onStart() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            getProfileUseCase().collectLatest { profile ->
                updateState {
                    copy(
                        profile = profile.toUiModel(),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onUpdateStoreName(name: String) {
        updateState { copy(profile = profile.copy(storeName = name)) }
        onSaveSettings()
    }

    fun onUpdateLanguage(language: String) {
        updateState { copy(profile = profile.copy(language = language)) }
        onSaveSettings()
    }

    fun onUpdateCurrency(currency: String) {
        updateState { copy(profile = profile.copy(currency = currency)) }
        onSaveSettings()
    }

    fun onUpdateTheme(isDark: Boolean) {
        updateState { copy(profile = profile.copy(isDarkTheme = isDark)) }
        onSaveSettings()
    }

    fun onUpdateLogo(uri: String) {
        updateState { copy(profile = profile.copy(storeLogoUri = uri)) }
        onSaveSettings()
    }

    fun onErrorMessage() = updateState { copy(profileError = null) }

    private fun onSaveSettings() {
        viewModelScope.launch {
            val settings = uiState.value.profile
            updateProfileUseCase.execute(settings.toDomainModel())
                .onFailure { updateState { copy(profileError = it.message) } }
        }
    }
}