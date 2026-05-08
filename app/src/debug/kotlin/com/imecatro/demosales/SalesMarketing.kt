package com.imecatro.demosales

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imecatro.demosales.ui.sales.R
import com.imecatro.demosales.ui.sales.list.model.SaleOnListUiModel
import com.imecatro.demosales.ui.sales.list.views.SalesListComposable
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme


@Preview(
    showBackground = true, locale = "es",
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun MarketingPreview() {
    PuntroSalesDemoTheme() {
        Surface(color = MaterialTheme.colorScheme.background) {
            SalesListComposable(todayTotal = 2500.0)
//            Box(contentAlignment = Alignment.BottomCenter) {
//
//                SalesListComposable(todayTotal = 2500.0, itemsSelectedQty = 6, showDownloadOptions = true)
//                Descargareporte()
//            }
        }
    }

}

@Preview(
    showBackground = true, locale = "es",
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun MarketingPreview2() {
    PuntroSalesDemoTheme() {
        Surface(color = MaterialTheme.colorScheme.background) {
            Box(contentAlignment = Alignment.BottomCenter) {

                SalesListComposable(
                    list = fakelist2,
                    todayTotal = 2500.0,
                    itemsSelectedQty = 6,
                    showDownloadOptions = true
                )
                Descargareporte()
            }
        }
    }

}

@Preview
@Composable
fun Descargareporte() {

    Column(
        Modifier
            .padding(horizontal = 0.dp)
            .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(10.dp))
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = {
                //reportState.salesFile?.open(context)
            }) {
                Text(stringResource(com.imecatro.demosales.ui.sales.R.string.txt_sales_report))
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = {
                //reportState.salesFile?.share(context)
            }) { Icon(Icons.Default.Share, null) }

            IconButton(onClick = {
//                reportState.salesFile?.download(context)
            }) { Icon(painterResource(com.imecatro.demosales.ui.sales.R.drawable.download), null) }

        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = {
//                reportState.groupedProductsFile?.open(context)
            }) {
                Text(stringResource(com.imecatro.demosales.ui.sales.R.string.txt_products_report))
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = {
//                reportState.groupedProductsFile?.share(context)
            }) { Icon(Icons.Default.Share, null) }
            IconButton(onClick = {
//                reportState.groupedProductsFile?.download(context)
            }) { Icon(painterResource(R.drawable.download), null) }

        }
    }
}

private val fakelist = listOf(
    SaleOnListUiModel(
        1,
        "Juan Pérez",
        "24/10/2023",
        1250.50,
        "Completado",
        Color(0xFF4CAF50),
        isSelected = false
    ),
    SaleOnListUiModel(
        2,
        "María García",
        "24/10/2023",
        450.00,
        "Completado",
        Color(0xFF4CAF50),
        isSelected = false
    ),
    SaleOnListUiModel(
        3,
        "Carlos Rodríguez",
        "23/10/2023",
        2100.00,
        "Pendiente",
        Color(0xFFFF9800),
        isSelected = false
    ),
    SaleOnListUiModel(
        4,
        "Ana Martínez",
        "23/10/2023",
        85.00,
        "Completado",
        Color(0xFF4CAF50),
        isSelected = false
    ),
    SaleOnListUiModel(
        5,
        "Luis Hernández",
        "22/10/2023",
        3200.00,
        "Pendiente",
        Color(0xFFFF9800),
        isSelected = false
    ),
    SaleOnListUiModel(6, "Sofía López", "22/10/2023", 120.00, "Cancelado", Color(0xFFF44336)),
    SaleOnListUiModel(
        7,
        "Diego Sánchez",
        "21/10/2023",
        560.00,
        "Completado",
        Color(0xFF4CAF50),
        isSelected = false
    ),
    SaleOnListUiModel(8, "Lucía Torres", "21/10/2023", 95.50, "Completado", Color(0xFF4CAF50)),
    SaleOnListUiModel(9, "Roberto Flores", "20/10/2023", 1500.00, "Completado", Color(0xFF4CAF50)),
    SaleOnListUiModel(10, "Elena Ramírez", "20/10/2023", 45.00, "Pendiente", Color(0xFFFF9800))
)

private val fakelist2 = listOf(
    SaleOnListUiModel(
        1,
        "Juan Pérez",
        "24/10/2023",
        1250.50,
        "Completado",
        Color(0xFF4CAF50),
        isSelected = true
    ),
    SaleOnListUiModel(
        2,
        "María García",
        "24/10/2023",
        450.00,
        "Completado",
        Color(0xFF4CAF50),
        isSelected = true
    ),
    SaleOnListUiModel(
        3,
        "Carlos Rodríguez",
        "23/10/2023",
        2100.00,
        "Pendiente",
        Color(0xFFFF9800),
        isSelected = true
    ),
    SaleOnListUiModel(
        4,
        "Ana Martínez",
        "23/10/2023",
        85.00,
        "Completado",
        Color(0xFF4CAF50),
        isSelected = true
    ),
    SaleOnListUiModel(
        5,
        "Luis Hernández",
        "22/10/2023",
        3200.00,
        "Pendiente",
        Color(0xFFFF9800),
        isSelected = true
    ),
    SaleOnListUiModel(6, "Sofía López", "22/10/2023", 120.00, "Cancelado", Color(0xFFF44336)),
    SaleOnListUiModel(
        7,
        "Diego Sánchez",
        "21/10/2023",
        560.00,
        "Completado",
        Color(0xFF4CAF50),
        isSelected = true
    ),
    SaleOnListUiModel(8, "Lucía Torres", "21/10/2023", 95.50, "Completado", Color(0xFF4CAF50)),
    SaleOnListUiModel(9, "Roberto Flores", "20/10/2023", 1500.00, "Completado", Color(0xFF4CAF50)),
    SaleOnListUiModel(10, "Elena Ramírez", "20/10/2023", 45.00, "Pendiente", Color(0xFFFF9800))
)
