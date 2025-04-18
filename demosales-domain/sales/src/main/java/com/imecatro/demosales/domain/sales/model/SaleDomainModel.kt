package com.imecatro.demosales.domain.sales.model


/**
 * Sale domain model
 *
 * @property id
 * @property clientId
 * @property date
 * @property productsList is a list of products of type [Order]
 * @property total is the amount of money [Double] the client will pay for all products on Cart
 * @property status see [OrderStatus]
 * @property note limited to 40 chars
 * @constructor Create empty Sale domain model
 */
data class SaleDomainModel(
    val id: Long,
    val clientId : Int,
    var date: String, //date when ticket was created
    var productsList: List<Order>, //contains the id of all products
    var total: Double,
    val status: OrderStatus = OrderStatus.INITIALIZED,
    val note : String = ""
)

data class Order(
    val id: Long,
    val productId :Int,
    val productName: String,
    val productPrice : Float,
    val qty: Float, //it must be float since some products can be 0.5 kg
    val imgUri : String? = null
)

enum class OrderStatus(val str: String) {
    INITIALIZED("Init"),
    PENDING("Pending"), //first status
    CANCEL("Cancel"), //if client cancel
    COMPLETED("Completed") //if order was paid and completed
}

