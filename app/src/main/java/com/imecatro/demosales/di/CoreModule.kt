package com.imecatro.demosales.di

import com.imecatro.demosales.datasource.room.di.RoomModule
import com.imecatro.demosales.domain.sales.add.repository.AddSaleDummyRepoImpl
import com.imecatro.demosales.domain.sales.list.repository.AllSalesRepository
import com.imecatro.demosales.domain.sales.list.repository.AllSalesRepositoryDummy
import com.imecatro.products.data.datasource.ProductsDao
import com.imecatro.products.data.repository.ProductsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

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
}
