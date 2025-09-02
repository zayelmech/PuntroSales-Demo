package com.imecatro.demosales.di

import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

@Module
@InstallIn(SingletonComponent::class)
object CoroutinesModuleProvider {

    @Provides
    fun providesCoroutineDispatcher(): CoroutineProvider = CoroutineDispatcherImpl()
}


class CoroutineDispatcherImpl : CoroutineProvider {
    override val io: CoroutineContext
        get() = Job() + Dispatchers.IO
    override val main: CoroutineContext
        get() = Job() + Dispatchers.Main

}