package com.imecatro.products.ui.catalog.state

import android.content.Context
import android.net.Uri
import com.imecatro.demosales.ui.theme.architect.ErrorUiModel
import com.imecatro.demosales.ui.theme.architect.Idle
import com.imecatro.demosales.ui.theme.architect.UiState
import com.imecatro.products.ui.catalog.model.ProductCatalogModel
import com.imecatro.products.ui.catalog.utils.buildProductsCatalogHtml
import com.imecatro.products.ui.catalog.utils.uriToDataUrlImage
import com.imecatro.products.ui.list.model.ProductUiModel
import java.io.File

data class ExportProductsState(
    val ids: List<Long> = emptyList(),
    val isProcessingCatalog: Boolean = false,

    val catalogReady: Boolean = false,
    val products: Map<String?, List<ProductUiModel>> = mapOf(),
) : UiState {
    fun getHtml(context: Context): String {

        val productsForHtml: Map<String?, List<ProductCatalogModel>> =
            products.mapValues { (_, list) ->
                list.map { p ->
                    val fileUri = Uri.fromFile(File(p.imageUrl?.toString()))

                    ProductCatalogModel(
                        name = p.name,
                        unit = p.unit,
                        price = p.price,
                        imageUrl = uriToDataUrlImage(context, fileUri),
                        category = p.category
                    )

                }
            }

        val html = buildProductsCatalogHtml(products = productsForHtml, columns = 4)

        return html
    }

    override fun isFetchingOrProcessingData(): Boolean {
        return isProcessingCatalog
    }

    override fun getError(): ErrorUiModel? {
        return null
    }

    companion object : Idle<ExportProductsState> {
        override val idle: ExportProductsState
            get() = ExportProductsState()

    }
}