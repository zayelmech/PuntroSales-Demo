package com.imecatro.demosales.ui.viewmodels

import androidx.lifecycle.viewModelScope
import com.imecatro.demosales.domain.core.profile.usecases.GetProfileUseCase
import com.imecatro.demosales.ui.theme.architect.BaseViewModel
import com.imecatro.demosales.ui.uistate.MainUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase
) : BaseViewModel<MainUiState>(MainUiState.idle) {

    override fun onStart() {
        viewModelScope.launch {
            getProfileUseCase().collect { profile ->
                updateState {
                    copy(
                        isDarkTheme = profile.isDarkTheme,
                        language = profile.language,
                        currency = profile.currency
                    )
                }
            }
        }
    }
}
