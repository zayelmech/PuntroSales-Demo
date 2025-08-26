package com.imecatro.demosales.ui.sales.list.mappers

import androidx.compose.ui.graphics.Color
import com.imecatro.demosales.domain.sales.list.model.SaleOnListDomainModel
import com.imecatro.demosales.domain.sales.model.OrderStatus
import com.imecatro.demosales.ui.sales.list.model.SaleOnListUiModel
import com.imecatro.demosales.ui.sales.list.model.SalesList
import java.time.Instant
import java.time.format.DateTimeFormatter


internal fun List<SaleOnListDomainModel>.toUiModel(): SalesList {
    return map {
        SaleOnListUiModel(
            id = it.id,
            clientName = it.clientName.ifEmpty { "System" },
            date = it.date.convertMillisToDate(),
            total = it.total,
            status = it.status.str,
            statusColor = it.status.toColor()
        )
    }
}

internal fun OrderStatus.toColor(): Color {
    return when(this){
        OrderStatus.INITIALIZED -> Color.Gray               // neutral, just created
        OrderStatus.PENDING     -> Color(0xFFFFA000)        // amber/orange = waiting
        OrderStatus.CANCEL      -> Color.Red// red = cancelled
        OrderStatus.COMPLETED   -> Color(0xFF388E3C)        // green = success
    }
}


fun Long.convertMillisToDate(): String {
    val instant = Instant.ofEpochMilli(this)
    // DateTimeFormatter.ISO_INSTANT correctly formats an Instant
    // to the full ISO 8601 format in UTC, ending with 'Z'.
    return DateTimeFormatter.ISO_INSTANT.format(instant)
}