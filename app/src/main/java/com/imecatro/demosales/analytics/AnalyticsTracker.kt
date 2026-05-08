package com.imecatro.demosales.analytics

interface AnalyticsTracker {

    fun trackEvent(
        name: String,
        params: Map<String, Any?>
    ) = Unit

    fun setUserId(userId: String?) = Unit

    fun recordException(throwable: Throwable) = Unit
}