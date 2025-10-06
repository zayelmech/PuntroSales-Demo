package com.imecatro.products.ui.list.uistate

import androidx.annotation.StringRes
import com.imecatro.products.ui.R

data class OrderedFilterState(
    @StringRes val text: Int,
    val isChecked: Boolean = false,
    val type: Type,
) {

    companion object {
        val filters = listOf(
            OrderedFilterState(R.string.filter_order_a_to_z, type = Type.NAME),
            OrderedFilterState(R.string.filter_order_z_to_a, type = Type.NAME_INVERSE),
            OrderedFilterState(R.string.filter_price_high_to_low, type = Type.PRICE),
            OrderedFilterState(R.string.filter_price_low_to_high, type = Type.PRICE_INVERSE),
            OrderedFilterState(R.string.filter_stock_low_to_high, type = Type.STOCK),
            OrderedFilterState(R.string.filter_stock_high_to_low, type = Type.STOCK_INVERSE),
            OrderedFilterState(R.string.filter_created_date_new_to_old, type = Type.DATE_INVERSE),
            OrderedFilterState(R.string.filter_created_date_old_to_new, true, type = Type.DATE)
        )
    }

    enum class Type {
        NAME, PRICE, STOCK, DATE, NAME_INVERSE, PRICE_INVERSE, STOCK_INVERSE, DATE_INVERSE
    }

}


fun List<OrderedFilterState>.checkElement(checkedItem: OrderedFilterState): List<OrderedFilterState> {
    return map {
        it.copy(isChecked = it == checkedItem)
    }
}
