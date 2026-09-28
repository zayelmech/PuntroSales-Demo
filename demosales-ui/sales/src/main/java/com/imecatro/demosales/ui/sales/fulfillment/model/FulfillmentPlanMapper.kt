package com.imecatro.demosales.ui.sales.fulfillment.model

import com.imecatro.demosales.domain.products.model.ProductDomainModel
import com.imecatro.demosales.domain.sales.details.SaleDetailsDomainModel
import com.imecatro.demosales.domain.sales.list.model.SaleOnListDomainModel
import com.imecatro.demosales.domain.sales.model.OrderStatus
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.util.Locale

internal data class FulfillmentSale(
    val sale: SaleOnListDomainModel,
    val details: SaleDetailsDomainModel
)

internal fun buildFulfillmentPlan(
    sales: List<FulfillmentSale>,
    products: List<ProductDomainModel>
): FulfillmentPlanUiState {
    val pending = sales.filter {
        it.sale.status == OrderStatus.PENDING && it.details.status == OrderStatus.PENDING
    }.distinctBy { it.sale.id }
    val inventory = products.associateBy { it.id }
    val quantityPrecision = MathContext(15, RoundingMode.HALF_UP)
    val lines = pending.flatMap { sale ->
        sale.details.list.filter { it.qty.isFinite() && it.qty > 0 }.map { sale.sale to it }
    }
    val groupedProducts = lines.groupBy { it.second.productId }.map { (productId, lines) ->
        val product = inventory[productId]
        val demand = lines.fold(BigDecimal.ZERO) { total, (_, order) ->
            total + BigDecimal.valueOf(order.qty)
        }.round(quantityPrecision)
        // Checkout already deducts PENDING orders. Restore their demand for this read-only
        // plan before comparing it with stock; subtracting demand again would overbuy.
        // Room stores Doubles: normalize only binary arithmetic noise (e.g. -0.20000000000000004).
        val available = product?.stock?.quantity?.takeIf { it.isFinite() }?.let {
            (BigDecimal.valueOf(it).round(quantityPrecision) + demand).round(quantityPrecision)
                .max(BigDecimal.ZERO)
        }
        FulfillmentProductUiModel(
            id = productId,
            name = product?.name?.takeIf { it.isNotBlank() } ?: lines.first().second.productName,
            unit = product?.unit?.takeIf { it.isNotBlank() },
            demand = demand,
            available = available,
            missing = available?.let { (demand - it).max(BigDecimal.ZERO) },
            orders = lines.groupBy { it.first.id }.map { (saleId, orders) ->
                FulfillmentOrderUiModel(
                    id = saleId,
                    clientName = orders.first().first.clientName,
                    quantity = orders.fold(BigDecimal.ZERO) { total, (_, order) ->
                        total + BigDecimal.valueOf(order.qty)
                    }
                )
            }.sortedBy { it.id }
        )
    }.sortedWith(
        compareByDescending<FulfillmentProductUiModel> { it.available == null || it.unit == null }
            .thenByDescending { it.missing?.signum() == 1 }
            .thenBy { it.name.lowercase(Locale.ROOT) }
            .thenBy { it.id }
    )
    return FulfillmentPlanUiState(
        isLoading = false,
        orderCount = pending.size,
        clientCount = pending.map { it.details.clientId }.filter { it > 0 }.distinct().size,
        products = groupedProducts
    )
}
