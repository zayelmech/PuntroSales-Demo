package com.imecatro.demosales.ui.sales.list.mappers

import android.os.Build
import androidx.annotation.RequiresApi
import com.imecatro.demosales.domain.sales.list.model.SaleOnListDomainModel
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
            clientName = it.clientName,
            date = it.date.convertMillisToDate(),
            total = it.total,
            status = it.status.str
        )
    }
}



fun Long.convertMillisToDate(): String {
    val instant = Instant.ofEpochMilli(this)
    val zoneId = ZoneId.systemDefault()
    val localDateTime = LocalDateTime.ofInstant(instant, zoneId)
    val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.getDefault())
    return localDateTime.format(formatter)
}