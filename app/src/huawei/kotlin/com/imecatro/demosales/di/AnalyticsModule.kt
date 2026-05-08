package com.imecatro.demosales.di

import com.imecatro.demosales.HuaweiAnalyticsTracker
import com.imecatro.demosales.analytics.AnalyticsTracker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {

    @Provides
    @Singleton
    fun provideAnalyticsTracker(): AnalyticsTracker {
        return HuaweiAnalyticsTracker()
    }
}