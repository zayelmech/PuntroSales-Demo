package com.imecatro.demosales

import com.imecatro.demosales.analytics.AnalyticsTracker

class HuaweiAnalyticsTracker : AnalyticsTracker {

    override fun trackEvent(
        name: String,
        params: Map<String, Any?>
    ) = Unit

    override fun setUserId(userId: String?) = Unit

    override fun recordException(throwable: Throwable) = Unit
}