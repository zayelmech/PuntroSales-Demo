package com.imecatro.demosales.ui.sales.add.mappers

import com.imecatro.demosales.domain.sales.model.OrderStatus
import com.imecatro.demosales.domain.sales.model.SaleDomainModel
import com.imecatro.demosales.ui.sales.add.model.ProductOnCartUiModel


//class SaleDomainToListProductOnCartUiMapper @Inject constructor(
//    private val getProductDetailsByIdUseCase: GetProductDetailsByIdUseCase
//) {
//
//    suspend fun toUiModel(saleModelDomain: SaleModelDomain): List<ProductOnCartUiModel> {
//
//        val li = mutableListOf<ProductOnCartUiModel>()
//        saleModelDomain.productsList.forEach {
//
//            val productDetails = getProductDetailsByIdUseCase(it.productId)
//            //TODO optimize performance
//
//            li.add(
//                ProductOnCartUiModel(
//                    product = ProductResultUiModel(
//                        id = saleModelDomain.id,
//                        name = productDetails?.name ?: "",
//                        price = productDetails?.price ?: 0f,
//                        imageUri = Uri.parse(productDetails?.imageUri),
//                    ),
//                    qty = it.qty,
//                    subtotal = productDetails?.price?.times(it.qty) ?: 0f
//                )
//            )
//        }
//        return li
//    }
//
//}

fun List<ProductOnCartUiModel>.toDomainModel(): SaleDomainModel {

    return SaleDomainModel(
        id = 0,
        clientId = 0,
        date = "",
        productsList = map { it.toOrderDomainModel() },
        total = 0.0,
        status = OrderStatus.PENDING
    )
}