package com.imecatro.demosales.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.products.usecases.GetAllProductsUseCase
import com.imecatro.demosales.domain.products.usecases.IsCatalogPublishedUseCase
import com.imecatro.demosales.domain.sales.details.GetDetailsOfSaleByIdUseCase
import com.imecatro.demosales.domain.sales.list.usecases.GetAllSalesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val store: OnboardingStore,
    getProducts: GetAllProductsUseCase,
    getSales: GetAllSalesUseCase,
    getDetails: GetDetailsOfSaleByIdUseCase,
    private val getCatalog: IsCatalogPublishedUseCase,
    dispatchers: CoroutineProvider
) : ViewModel() {
    val progress = store.progress
    private val mutableShowIntroduction = MutableStateFlow(false)
    val showIntroduction = mutableShowIntroduction.asStateFlow()
    private val mutableProductIds = MutableStateFlow<List<Long>>(emptyList())
    val productIds = mutableProductIds.asStateFlow()
    private val mutableCatalogPublished = MutableStateFlow(false)
    val catalogPublished = mutableCatalogPublished.asStateFlow()

    init {
        viewModelScope.launch { mutableShowIntroduction.value = store.claimPresentation() }
        viewModelScope.launch(dispatchers.io) {
            safely {
                getProducts().collect { products ->
                    mutableProductIds.value = products.mapNotNull { it.id }
                    store.productsSaved(products.size)
                }
            }
        }
        viewModelScope.launch(dispatchers.io) {
            safely {
                getSales().collect { sales ->
                    if (OnboardingStep.ORDER !in progress.value.steps) {
                        for (sale in sales) {
                            if (isSavedOrder(sale.status, true)) {
                                // A single inaccessible ticket must not prevent inspecting others.
                                safely {
                                    val details = getDetails(sale.id)
                                    if (isSavedOrder(details.status, details.list.isNotEmpty())) {
                                        store.complete(OnboardingStep.ORDER)
                                    }
                                }
                                if (OnboardingStep.ORDER in progress.value.steps) break
                            }
                        }
                    }
                }
            }
        }
    }

    fun dismissIntroduction() { mutableShowIntroduction.value = false }

    fun refreshCatalog() {
        viewModelScope.launch {
            getCatalog.execute(Unit).onSuccess {
                mutableCatalogPublished.value = it != null
                if (it != null) store.complete(OnboardingStep.CATALOG)
            }
        }
    }

    fun planLoaded(isLoading: Boolean, hasError: Boolean, needsReview: Boolean) {
        if (isSuccessfulPlan(isLoading, hasError, needsReview)) {
            viewModelScope.launch { store.complete(OnboardingStep.FULFILLMENT) }
        }
    }

    fun acknowledgeCompletion() {
        viewModelScope.launch { store.acknowledgeCompletion() }
    }
}

private suspend fun safely(block: suspend () -> Unit) {
    try { block() } catch (cancelled: CancellationException) { throw cancelled }
    catch (_: Exception) { /* Missing evidence never completes a step. Later saves still record it. */ }
}
