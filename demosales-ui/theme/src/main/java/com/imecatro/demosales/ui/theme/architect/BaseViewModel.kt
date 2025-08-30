package com.imecatro.demosales.ui.theme.architect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

abstract class BaseViewModel<T>(idle: T) : ViewModel() {

    private val _uiState: MutableStateFlow<T> =
        MutableStateFlow(idle)

    val uiState: StateFlow<T> = _uiState.onStart { onStart() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(30000L), idle)


    open fun onStart() = Unit

    fun updateState(state: T.() -> T) = _uiState.update(state)
}