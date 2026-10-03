package com.imecatro.demosales.onboarding

import com.imecatro.demosales.domain.products.model.ProductDomainModel
import com.imecatro.demosales.domain.products.repository.CatalogRepository
import com.imecatro.demosales.domain.products.repository.ProductsRepository
import com.imecatro.demosales.domain.sales.add.repository.AddSaleRepository
import com.imecatro.demosales.domain.sales.model.SaleDomainModel
import kotlinx.coroutines.flow.first

// Decorate existing domain contracts at the app composition root. Feature UI and
// data modules never depend on onboarding, and failed writes never earn a milestone.
class OnboardingProductsRepository(
    private val delegate: ProductsRepository,
    private val store: OnboardingStore
) : ProductsRepository by delegate {
    override suspend fun addProduct(product: ProductDomainModel) {
        delegate.addProduct(product)
        store.productsSaved(delegate.getAllProducts().first().size)
    }

    override suspend fun deleteProductById(id: Long) {
        // Preserve a pre-existing milestone even if deletion is the first action.
        store.productsSaved(delegate.getAllProducts().first().size)
        delegate.deleteProductById(id)
        store.productsSaved(delegate.getAllProducts().first().size)
    }
}

class OnboardingSalesRepository(
    private val delegate: AddSaleRepository,
    private val store: OnboardingStore
) : AddSaleRepository by delegate {
    override suspend fun saveSale(sale: SaleDomainModel) {
        delegate.saveSale(sale)
        if (isSavedOrder(sale.status, sale.productsList.isNotEmpty())) {
            store.complete(OnboardingStep.ORDER)
        }
    }
}

class OnboardingCatalogRepository(
    private val delegate: CatalogRepository,
    private val store: OnboardingStore
) : CatalogRepository by delegate {
    override suspend fun createsFinalUrl(jsonCatalogUrl: String): String {
        val url = delegate.createsFinalUrl(jsonCatalogUrl)
        if (url.isNotBlank()) store.complete(OnboardingStep.CATALOG)
        return url
    }

    override suspend fun isCatalogPublished(): Boolean {
        val published = delegate.isCatalogPublished()
        if (published) store.complete(OnboardingStep.CATALOG)
        return published
    }

    override suspend fun getPublishedCatalog() = delegate.getPublishedCatalog().also {
        if (it != null) store.complete(OnboardingStep.CATALOG)
    }
}
