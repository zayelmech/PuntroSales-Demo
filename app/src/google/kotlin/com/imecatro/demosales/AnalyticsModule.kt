package com.imecatro.demosales

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.imecatro.demosales.analytics.AnalyticsTracker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {

    @Provides
    @Singleton
    fun provideAnalyticsTracker(
        @ApplicationContext context: Context
    ): AnalyticsTracker {
        return FirebaseAnalyticsTracker(
            analytics = FirebaseAnalytics.getInstance(context),
            crashlytics = FirebaseCrashlytics.getInstance()
        )
    }
}