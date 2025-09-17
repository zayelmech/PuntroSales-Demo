package com.imecatro.products.ui.list.model

import androidx.annotation.StringRes
import com.imecatro.products.ui.R

data class OrderedFilterUiModel(
    @StringRes val text: Int,
    val isChecked: Boolean = false,
    val type: Type,
) {

    companion object {
        val filters = listOf(
            OrderedFilterUiModel(R.string.filter_order_a_to_z, type = Type.NAME),
            OrderedFilterUiModel(R.string.filter_order_z_to_a, type = Type.NAME_INVERSE),
            OrderedFilterUiModel(R.string.filter_price_high_to_low, type = Type.PRICE),
            OrderedFilterUiModel(R.string.filter_price_low_to_high, type = Type.PRICE_INVERSE),
            OrderedFilterUiModel(R.string.filter_stock_low_to_high, type = Type.STOCK),
            OrderedFilterUiModel(R.string.filter_stock_high_to_low, type = Type.STOCK_INVERSE),
            OrderedFilterUiModel(R.string.filter_created_date_new_to_old, type = Type.DATE_INVERSE),
            OrderedFilterUiModel(R.string.filter_created_date_old_to_new, true, type = Type.DATE)
        )
    }

    enum class Type {
        NAME, PRICE, STOCK, DATE, NAME_INVERSE, PRICE_INVERSE, STOCK_INVERSE, DATE_INVERSE
    }

}


fun List<OrderedFilterUiModel>.checkElement(checkedItem: OrderedFilterUiModel): List<OrderedFilterUiModel> {
    return map {
        it.copy(isChecked = it == checkedItem)
    }
}
