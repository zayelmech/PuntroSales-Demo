package com.imecatro.demosales.domain.sales.model

data class SaleModelDomain(
    val id: Int,
    val date: String, //date when ticket was created
    val productsIdMap: List<Order>, //contains the id of all products
    val total: Double,
    val status: OrderStatus = OrderStatus.PENDING,
)

data class Order(
    val productId: Int,
    val qty: Float, //it must be float since some products can be 0.5 kg
)

enum class OrderStatus(val str: String) {
    PENDING("Pending"), //first status
    CANCEL("Cancel"), //if client cancel
    COMPLETED("Completed") //if order was paid and completed
}

