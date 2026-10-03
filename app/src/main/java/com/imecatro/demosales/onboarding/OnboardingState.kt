package com.imecatro.demosales.onboarding

import com.imecatro.demosales.domain.sales.model.OrderStatus

// Deliberately independent of the application's versionCode.
const val ONBOARDING_VERSION = 1

enum class OnboardingStep(val key: String) {
    BUSINESS("business"), PRODUCTS("products"), ORDER("order"),
    FULFILLMENT("fulfillment"), CATALOG("catalog")
}

data class OnboardingProgress(
    val presentedVersion: Int = 0,
    val steps: Set<OnboardingStep> = emptySet(),
    val productCount: Int = 0,
    val completed: Boolean = false,
    val completionAcknowledged: Boolean = false
) {
    val needsPresentation: Boolean get() = presentedVersion < ONBOARDING_VERSION
    val nextStep: OnboardingStep? get() = OnboardingStep.entries.firstOrNull { it !in steps }

    fun complete(step: OnboardingStep): OnboardingProgress {
        val updated = steps + step
        return copy(steps = updated, completed = completed || updated.size == OnboardingStep.entries.size)
    }

    fun withProducts(count: Int): OnboardingProgress {
        val updated = copy(productCount = count.coerceIn(0, 5))
        return if (count >= 5) updated.complete(OnboardingStep.PRODUCTS) else updated
    }
}

internal fun isSavedOrder(status: OrderStatus, hasProducts: Boolean): Boolean =
    hasProducts && (status == OrderStatus.PENDING || status == OrderStatus.COMPLETED)

internal fun isConfirmedLegacyBusiness(savedName: String?): Boolean =
    !savedName.isNullOrBlank() && !savedName.trim().equals("Puntro Sales", ignoreCase = true)

internal fun isSuccessfulPlan(isLoading: Boolean, hasError: Boolean, needsReview: Boolean): Boolean =
    !isLoading && !hasError && !needsReview
