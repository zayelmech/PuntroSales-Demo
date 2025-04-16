package com.imecatro.demosales.ui.theme.architect


interface UiState {

    fun isFetchingOrProcessingData(): Boolean

    fun getError(): ErrorUiModel?
}

val UiState.isLoading get() = isFetchingOrProcessingData()

val UiState.error get() = getError()




/**
 * Represents an entity that has an idle state of type [T].
 * This interface defines a property [idle] which holds the idle state of the object.
 *
 * @param T The type of the idle state.
 */
interface Idle<T> {

    /**
     * The idle state of type [T].
     * This property represents the current idle state of the object implementing this interface.
     */
    val idle: T
}
