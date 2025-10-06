package com.imecatro.demosales.ui.sales.list.model

import androidx.annotation.StringRes
import com.imecatro.demosales.domain.sales.model.OrderStatus
import com.imecatro.demosales.ui.sales.R


data class StatusFilterUiModel(
    @StringRes val text: Int,
    val isChecked: Boolean = false,
    val type: Type,
) {

    companion object {
        val filters = listOf(
            StatusFilterUiModel(R.string.txt_filter_draft, type = Type.DRAFT),
            StatusFilterUiModel(R.string.txt_filter_pending, type = Type.PENDING),
            StatusFilterUiModel(R.string.txt_filter_completed, type = Type.COMPLETED),
            StatusFilterUiModel(R.string.txt_filter_cancelled, type = Type.CANCELLED),
        )
    }

    enum class Type {
        DRAFT, PENDING, COMPLETED, CANCELLED

    }

}

fun StatusFilterUiModel.toDomain(): String {
    return when(this.type){
        StatusFilterUiModel.Type.DRAFT -> OrderStatus.INITIALIZED.str
        StatusFilterUiModel.Type.PENDING -> OrderStatus.PENDING.str
        StatusFilterUiModel.Type.COMPLETED -> OrderStatus.COMPLETED.str
        StatusFilterUiModel.Type.CANCELLED -> OrderStatus.CANCEL.str
    }
}
