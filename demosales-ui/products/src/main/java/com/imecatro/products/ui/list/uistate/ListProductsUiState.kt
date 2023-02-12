package com.imecatro.products.ui.list.uistate

sealed class ListProductsUiState(){
    object Initialized: ListProductsUiState()
    object Loading : ListProductsUiState()
    data class Success(val items : Int) : ListProductsUiState()
    data class Error(val message: String) : ListProductsUiState()

}
