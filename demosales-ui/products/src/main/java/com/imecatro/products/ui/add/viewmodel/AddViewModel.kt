package com.imecatro.products.ui.add.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imecatro.domain.products.repository.ProductsRepository
import com.imecatro.domain.products.usecases.GetListOfCurrenciesUseCase
import com.imecatro.domain.products.usecases.GetListOfUnitsUseCase
import com.imecatro.products.ui.add.mappers.toDomain
import com.imecatro.products.ui.add.model.AddProductUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "AddViewModel"
class AddViewModel @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val getListOfCurrenciesUseCase: GetListOfCurrenciesUseCase = GetListOfCurrenciesUseCase(),
    private val getListOfUnitsUseCase: GetListOfUnitsUseCase = GetListOfUnitsUseCase()
) : ViewModel() {

    fun onSaveAction(addProductUiModel: AddProductUiModel) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "onSaveAction: ${addProductUiModel.imageUri?.toString()}")
            productsRepository.addProduct(addProductUiModel.toDomain())
        }

    }

    fun getUnities(): List<String> {
        return getListOfUnitsUseCase() //listOf("pz", "kg", "g", "m", "cm")
    }

    fun getCurrencies(): List<String> {
        return getListOfCurrenciesUseCase() //listOf("USD", "MXN", "EUR", "GBP")
    }
}

