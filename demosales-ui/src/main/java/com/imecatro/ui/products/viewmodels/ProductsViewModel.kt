package com.imecatro.ui.products.viewmodels

import androidx.lifecycle.ViewModel
import com.imecatro.ui.products.model.ProductUiModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductsViewModel(
//    private val productsRepository: ProductsRepository
) : ViewModel() {


    private val _productsList: MutableStateFlow<List<ProductUiModel>> =
        MutableStateFlow(getAllProducts())

    val productsList: StateFlow<List<ProductUiModel>> = _productsList.asStateFlow()

    @Volatile
    private var fakeList = mutableListOf<ProductUiModel>()

    private fun getAllProducts(): List<ProductUiModel> {
        CoroutineScope(Dispatchers.IO).launch {
            for (i in 1..10) {
                fakeList.add(ProductUiModel(i, "Product Name $i", "3.00", "pz", "null"))
            }
            _productsList.value = fakeList
        }

        return fakeList
    }

    fun addRandom() {
        CoroutineScope(Dispatchers.IO).launch {
            val newList: List<ProductUiModel> = _productsList.value.toMutableList().apply {
                val idRdm = (19..100).random()
                add(ProductUiModel(idRdm, "Product Random $idRdm", "1.00", "kg", ""))
            }
            _productsList.emit(newList)
        }
    }

    fun onDeleteAction(id: Int?) {
        CoroutineScope(Dispatchers.IO).launch {

            val newList: List<ProductUiModel> = _productsList.value.toMutableList().apply {
                removeIf {
                    it.id == id
                }
            }
            _productsList.emit(newList)
        }
    }

    fun getDetailsById(id: Int?): ProductUiModel? {
        return _productsList.value.find { it.id == id }

    }

}