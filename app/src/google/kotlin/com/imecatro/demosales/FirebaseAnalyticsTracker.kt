package com.imecatro.demosales

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.imecatro.demosales.analytics.AnalyticsTracker

class FirebaseAnalyticsTracker(
    private val analytics: FirebaseAnalytics,
    private val crashlytics: FirebaseCrashlytics
) : AnalyticsTracker {

    override fun trackEvent(
        name: String,
        params: Map<String, Any?>
    ) {
        val bundle = Bundle()

        params.forEach { (key, value) ->
            when (value) {
                is String -> bundle.putString(key, value)
                is Int -> bundle.putInt(key, value)
                is Long -> bundle.putLong(key, value)
                is Double -> bundle.putDouble(key, value)
                is Float -> bundle.putFloat(key, value)
                is Boolean -> bundle.putBoolean(key, value)
                null -> Unit
                else -> bundle.putString(key, value.toString())
            }
        }

        analytics.logEvent(name, bundle)
    }

    override fun setUserId(userId: String?) {
        analytics.setUserId(userId)
        crashlytics.setUserId(userId.orEmpty())
    }

    override fun recordException(throwable: Throwable) {
        crashlytics.recordException(throwable)
    }
}