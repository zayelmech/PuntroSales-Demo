package com.imecatro.demosales.profile.ui.viewmodels

import androidx.lifecycle.viewModelScope
import com.imecatro.demosales.domain.core.architecture.usecase.onAny
import com.imecatro.demosales.profile.domain.usecases.GetProfileUseCase
import com.imecatro.demosales.profile.domain.usecases.UpdateProfileUseCase
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
                        profile = profile,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onUpdateStoreName(name: String) = updateState {
        copy(profile = profile.copy(storeName = name))
    }

    fun onUpdateLanguage(language: String) = updateState {
        copy(profile = profile.copy(language = language))
    }

    fun onUpdateCurrency(currency: String) = updateState {
        copy(profile = profile.copy(currency = currency))
    }


    fun onUpdateTheme(isDark: Boolean) = updateState {
        copy(profile = profile.copy(isDarkTheme = isDark))
    }

    fun onUpdateLogo(uri: String) = updateState {
        copy(profile = profile.copy(storeLogoUri = uri))
    }

    fun onErrorMessage()= updateState { copy(profileError = null) }

    fun onSaveSettings() {
        viewModelScope.launch {
            val profile = uiState.value.profile
            updateState { copy(isLoading = true) }
            updateProfileUseCase.execute(profile)
                .onAny { updateState { copy(isLoading = false) } }
                .onFailure { updateState { copy(profileError = it.message) } }
        }
    }
}