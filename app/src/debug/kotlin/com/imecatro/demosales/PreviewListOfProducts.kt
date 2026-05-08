package com.imecatro.demosales

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme
import com.imecatro.products.ui.list.model.ProductUiModel
import com.imecatro.products.ui.list.views.ListOfProducts

fun fakeProductsList2(qty: Int): List<ProductUiModel> {
    val products = listOf(
        Triple("Bolso Elegante 'Victoria'", "450.00", "Bolsos"),
        Triple("Zapatillas de Tacón Nude", "680.00", "Calzado"),
        Triple("Set de Brochas Premium", "250.00", "Maquillaje"),
        Triple("Cartera Piel Sintética", "180.00", "Accesorios"),
        Triple("Tenis Urbanos Blancos", "850.00", "Calzado"),
        Triple("Paleta de Sombras 'Sunset'", "320.00", "Maquillaje"),
        Triple("Bolsa de Mano Casual", "380.00", "Bolsos"),
        Triple("Lentes de Sol Cat Eye", "150.00", "Accesorios"),
        Triple("Botines de Gamuza Café", "720.00", "Calzado"),
        Triple("Labial Mate Larga Duración", "120.00", "Maquillaje"),
        Triple("Reloj Rose Gold", "290.00", "Accesorios"),
        Triple("Sandalias con Plataforma", "540.00", "Calzado"),
        Triple("Mochila Fashion Pastel", "420.00", "Bolsos"),
        Triple("Cinturón de Cadena Dorado", "95.00", "Accesorios"),
        Triple("Serum Facial Hidratante", "280.00", "Skin Care")
    )

    return List(qty) { i ->
        val (name, price, category) = products[i % products.size]
        ProductUiModel(
            id = i.toLong() + 100,
            name = if (i < products.size) name else "$name ${i / products.size + 1}",
            price = price,
            unit = "pz",
            stock = (5..20).random().toString(),
            imageUrl = null,
            category = category
        )
    }
}


@Preview(showBackground = true, showSystemUi = false,
    device = "spec:parent=pixel_5,navigation=buttons",
    uiMode = Configuration.UI_MODE_TYPE_NORMAL, locale = "es"
)
@Composable
fun PreviewListOfProducts() {
    PuntroSalesDemoTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            ListOfProducts(list = fakeProductsList2(20), false)
        }
    }
}