package com.imecatro.demosales.ui.sales.fulfillment

import com.imecatro.demosales.domain.products.model.ProductDomainModel
import com.imecatro.demosales.domain.products.model.ProductStockDomainModel
import com.imecatro.demosales.domain.sales.details.SaleDetailsDomainModel
import com.imecatro.demosales.domain.sales.list.model.SaleOnListDomainModel
import com.imecatro.demosales.domain.sales.model.Order
import com.imecatro.demosales.domain.sales.model.OrderStatus
import com.imecatro.demosales.ui.sales.fulfillment.model.FulfillmentSale
import com.imecatro.demosales.ui.sales.fulfillment.model.buildFulfillmentPlan
import com.imecatro.demosales.ui.sales.fulfillment.model.shoppingListText
import org.junit.Assert.*
import org.junit.Test
import java.util.Locale

class FulfillmentPlanMapperTest {
    @Test
    fun `groups products by ID and preserves customer and order quantities`() {
        val state = buildFulfillmentPlan(
            listOf(
                sale(1, listOf(order(1, 2.5), order(1, 0.5))),
                sale(2, listOf(order(1, 4.0), order(2, 1.0)))
            ),
            listOf(product(1, -2.0), product(2, 0.0))
        )
        val coffee = state.products.first { it.id == 1L }
        assertEquals(2, state.orderCount)
        assertEquals(1, state.clientCount)
        assertEquals(2, state.products.size)
        assertEquals(7.0, coffee.demand.toDouble(), 0.0)
        assertEquals(5.0, coffee.available!!.toDouble(), 0.0)
        assertEquals(2.0, coffee.missing!!.toDouble(), 0.0)
        assertEquals(listOf(1L, 2L), coffee.orders.map { it.id })
        assertEquals(listOf(3.0, 4.0), coffee.orders.map { it.quantity.toDouble() })
        assertEquals("María", coffee.orders.first().clientName)
    }

    @Test
    fun `pending stock reservations are not subtracted twice`() {
        val state = buildFulfillmentPlan(listOf(sale(1, listOf(order(1, 8.0)))), listOf(product(1, 2.0)))
        val row = state.products.single()
        assertEquals(10.0, row.available!!.toDouble(), 0.0)
        assertEquals(0.0, row.missing!!.toDouble(), 0.0)
        assertFalse(state.canShare)
    }

    @Test
    fun `zero net stock covers precisely the pending demand`() {
        val row = buildFulfillmentPlan(listOf(sale(1, listOf(order(1, 8.0)))), listOf(product(1, 0.0))).products.single()
        assertEquals(8.0, row.available!!.toDouble(), 0.0)
        assertEquals(0.0, row.missing!!.toDouble(), 0.0)
    }

    @Test
    fun `shortage never exceeds pending demand even with older stock debt`() {
        val row = buildFulfillmentPlan(listOf(sale(1, listOf(order(1, 8.0)))), listOf(product(1, -20.0))).products.single()
        assertEquals(0.0, row.available!!.toDouble(), 0.0)
        assertEquals(8.0, row.missing!!.toDouble(), 0.0)
    }

    @Test
    fun `ignores drafts completed and cancelled orders and changed statuses`() {
        val sales = OrderStatus.entries.mapIndexed { index, status ->
            sale(index.toLong(), listOf(order(1, 2.0)), status)
        } + sale(9, listOf(order(1, 10.0))).let {
            it.copy(details = it.details.copy(status = OrderStatus.CANCEL))
        }
        val state = buildFulfillmentPlan(sales, listOf(product(1, -1.0)))
        assertEquals(1, state.orderCount)
        assertEquals(2.0, state.products.single().demand.toDouble(), 0.0)
    }

    @Test
    fun `fractional quantities avoid double precision artifacts in purchase text`() {
        val state = buildFulfillmentPlan(
            listOf(sale(1, listOf(order(1, 0.1), order(1, 0.2)))),
            listOf(product(1, 0.1 - 0.3, unit = "kg"))
        )
        assertEquals(0.3, state.products.single().demand.toDouble(), 0.0)
        assertEquals("Compra\n\n• Café: 0.2 kg", state.shoppingListText("Compra", Locale.US))
        assertEquals("Compra\n\n• Café: 0,2 kg", state.shoppingListText("Compra", Locale.GERMANY))
    }

    @Test
    fun `shares only shortages with units and without customer information`() {
        val state = buildFulfillmentPlan(
            listOf(sale(1, listOf(order(1, 5.0), order(2, 2.0)))),
            listOf(product(1, -3.0, unit = "kg"), product(2, 10.0, name = "Miel"))
        )
        val text = state.shoppingListText("Compra", Locale.US)
        assertEquals("Compra\n\n• Café: 3 kg", text)
        assertFalse(text.contains("María"))
        assertFalse(text.contains("Miel"))
    }

    @Test
    fun `deleted products retain historical demand but prevent incomplete sharing`() {
        val state = buildFulfillmentPlan(listOf(sale(1, listOf(order(1, 4.0)))), emptyList())
        val row = state.products.single()
        assertEquals("Café original", row.name)
        assertEquals(4.0, row.demand.toDouble(), 0.0)
        assertNull(row.available)
        assertNull(row.missing)
        assertNull(row.unit)
        assertTrue(state.needsReview)
        assertFalse(state.canShare)
        assertEquals("", state.shoppingListText("Compra"))
    }

    @Test
    fun `missing unit and invalid stock require review`() {
        val sales = listOf(sale(1, listOf(order(1, 4.0))))
        assertFalse(buildFulfillmentPlan(sales, listOf(product(1, -2.0, unit = null))).canShare)
        assertTrue(buildFulfillmentPlan(sales, listOf(product(1, Double.NaN))).needsReview)
    }

    @Test
    fun `empty plan cannot be shared`() {
        val state = buildFulfillmentPlan(emptyList(), emptyList())
        assertFalse(state.isLoading)
        assertEquals(0, state.orderCount)
        assertTrue(state.products.isEmpty())
        assertFalse(state.canShare)
    }

    @Test
    fun `floating point stock noise does not create phantom purchases`() {
        val state = buildFulfillmentPlan(
            listOf(sale(1, listOf(order(1, 0.1), order(1, 0.2)))),
            listOf(product(1, 0.3 - 0.1 - 0.2))
        )
        assertTrue(state.shoppingList.isEmpty())
        assertFalse(state.canShare)
    }

    @Test
    fun `same product name does not merge distinct product IDs or units`() {
        val state = buildFulfillmentPlan(
            listOf(sale(1, listOf(order(1, 2.0), order(2, 3.0)))),
            listOf(product(1, -1.0, unit = "kg"), product(2, -2.0, unit = "pz"))
        )
        assertEquals(2, state.products.size)
        assertEquals(setOf("kg", "pz"), state.products.map { it.unit }.toSet())
        assertEquals(2, state.shoppingList.size)
    }
}

internal fun sale(
    id: Long,
    orders: List<Order>,
    status: OrderStatus = OrderStatus.PENDING
) = FulfillmentSale(
    SaleOnListDomainModel(id, "María", 0, 0.0, status),
    SaleDetailsDomainModel(orders, clientId = 10, status = status)
)

internal fun order(productId: Long, quantity: Double) =
    Order(0, productId, "Café original", 25.0, quantity)

internal fun product(id: Long, stock: Double, name: String = "Café", unit: String? = "pz") =
    ProductDomainModel(id, name, 25.0, "MXN", unit, ProductStockDomainModel(stock, 0.0, emptyList()), "", null, null, null)
