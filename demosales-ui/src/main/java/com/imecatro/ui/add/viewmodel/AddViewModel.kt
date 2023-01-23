package com.imecatro.ui.add.viewmodel

import androidx.lifecycle.ViewModel
import com.imecatro.ui.add.model.AddProductUiModel

class AddViewModel : ViewModel() {

    fun onSaveAction(addProductUiModel: AddProductUiModel) {


    }

    fun getUnities(): List<String> {
        return listOf("pz", "kg", "g", "m", "cm")
    }

    fun getCurrencies(): List<String> {
        return listOf("USD", "MXN", "EUR", "GBP")
    }
}