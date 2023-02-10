package com.imecatro.demosales.ui.sales.add.mappers

import android.net.Uri
import com.imecatro.demosales.domain.products.usecases.GetProductDetailsByIdUseCase
import com.imecatro.demosales.domain.sales.model.SaleModelDomain
import com.imecatro.demosales.ui.sales.add.model.ProductOnCartUiModel
import com.imecatro.demosales.ui.sales.add.model.ProductResultUiModel
import javax.inject.Inject


class SaleDomainToListProductOnCartUiMapper @Inject constructor(
    private val getProductDetailsByIdUseCase: GetProductDetailsByIdUseCase
) {

    suspend fun toUiModel(saleModelDomain: SaleModelDomain): List<ProductOnCartUiModel> {

        val li = mutableListOf<ProductOnCartUiModel>()
        saleModelDomain.productsList.forEach {

            val productDetails = getProductDetailsByIdUseCase(it.productId)
            //TODO optimize performance

            li.add(
                ProductOnCartUiModel(
                    product = ProductResultUiModel(
                        id = saleModelDomain.id,
                        name = productDetails?.name ?: "",
                        price = productDetails?.price ?: 0f,
                        imageUri = Uri.parse(productDetails?.imageUri),
                    ),
                    qty = it.qty,
                    subtotal = productDetails?.price?.times(it.qty) ?: 0f
                )
            )
        }
        return li
    }

}