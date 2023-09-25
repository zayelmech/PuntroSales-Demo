package com.imecatro.demosales.domain.sales.model

data class SaleDomainModel(
    val id: Long,
    val clientId : Int,
    var date: String, //date when ticket was created
    var productsList: List<Order>, //contains the id of all products
    var total: Double,
    val status: OrderStatus = OrderStatus.PENDING
)

data class Order(
    val productId :Int,
    val productName: String,
    val productPrice : Float,
    val qty: Float, //it must be float since some products can be 0.5 kg
)

enum class OrderStatus(val str: String) {
    INITIALIZED("Init"),
    PENDING("Pending"), //first status
    CANCEL("Cancel"), //if client cancel
    COMPLETED("Completed") //if order was paid and completed
}

