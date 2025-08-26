package com.imecatro.demosales.ui.sales.list.mappers

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import com.imecatro.demosales.domain.sales.list.model.SaleOnListDomainModel
import com.imecatro.demosales.domain.sales.model.OrderStatus
import com.imecatro.demosales.ui.sales.list.model.SaleOnListUiModel
import com.imecatro.demosales.ui.sales.list.model.SalesList
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale


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
    val zoneId = ZoneId.systemDefault()
    val localDateTime = LocalDateTime.ofInstant(instant, zoneId)
    val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.getDefault())
    return localDateTime.format(formatter)
}