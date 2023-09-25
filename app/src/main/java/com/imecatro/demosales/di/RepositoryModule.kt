package com.imecatro.demosales.di

import com.imecatro.demosales.data.sales.add.repository.AddSaleRepositoryImpl
import com.imecatro.demosales.data.sales.datasource.OrdersRoomDao
import com.imecatro.demosales.data.sales.datasource.SalesRoomDao
import com.imecatro.demosales.data.sales.details.DetailsSaleRepositoryImpl
import com.imecatro.demosales.data.sales.list.repository.AllSalesRepositoryImpl
import com.imecatro.products.data.repository.ProductsRepositoryImpl
import com.imecatro.demosales.domain.products.repository.ProductsRepository
import com.imecatro.demosales.domain.sales.add.repository.AddSaleDummyRepoImpl
import com.imecatro.demosales.domain.sales.add.repository.AddSaleRepository
import com.imecatro.demosales.domain.sales.details.DetailsSaleRepository
import com.imecatro.demosales.domain.sales.list.repository.AllSalesRepository
import com.imecatro.demosales.domain.sales.list.repository.AllSalesRepositoryDummy
import com.imecatro.products.data.datasource.ProductsDao
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {


    @Provides
    fun provideRoomRepositoryImplementation(dao: ProductsDao): ProductsRepository =
        ProductsRepositoryImpl(dao)

    @Provides
    fun providesSalesListRepository(
        dao: SalesRoomDao,
        ordersRoomDao: OrdersRoomDao
    ): AllSalesRepository =
        AllSalesRepositoryImpl(dao, ordersRoomDao)

    @Provides
    @Singleton
    fun providesAddSaleRepoImpl(
        dao: SalesRoomDao,
        ordersRoomDao: OrdersRoomDao
    ): AddSaleRepository = AddSaleRepositoryImpl(dao, ordersRoomDao)


    @Provides
    fun providesDetailsSaleRepoImpl(
        salesRoomDao: SalesRoomDao,
        ordersRoomDao: OrdersRoomDao
    ): DetailsSaleRepository = DetailsSaleRepositoryImpl(salesRoomDao,ordersRoomDao)

}