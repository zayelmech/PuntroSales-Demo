package com.imecatro.demosales.di

import com.imecatro.demosales.datasource.room.di.RoomModule
import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

@Module(includes = [RoomModule::class])
@InstallIn(SingletonComponent::class)
interface CoreModule

@Module
@InstallIn(SingletonComponent::class)
class FakeRepoImpl() {
//    @Provides
//    fun provideAddSaleDummyRepoImpl(): AddSaleDummyRepoImpl = AddSaleDummyRepoImpl()
//
//    @Provides
//    fun provideRoomRepositoryImplementation(dao: ProductsDao): ProductsRepositoryImpl =
//        ProductsRepositoryImpl(dao)

//    @Provides
//    fun provideAllSalesDummyImpl(): AllSalesRepository = AllSalesRepositoryDummy()


    @Provides
    fun providesCoroutineDispatcher(): CoroutineProvider = CoroutineDispatcherImpl()

//    @Singleton
//    @Provides
//    fun providesClientsRepository(): ClientsRepository = DummyClientsRepositoryImpl()
}


class CoroutineDispatcherImpl : CoroutineProvider {
    override val io: CoroutineContext
        get() = Job() + Dispatchers.IO
    override val main: CoroutineContext
        get() = Job() + Dispatchers.Main

}