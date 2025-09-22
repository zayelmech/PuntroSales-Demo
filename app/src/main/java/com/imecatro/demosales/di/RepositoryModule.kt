package com.imecatro.demosales.di

import com.imecatro.demosales.data.clients.datasource.ClientsDao
import com.imecatro.demosales.data.clients.repository.ClientsRepositoryImpl
import com.imecatro.demosales.data.sales.add.repository.AddSaleRepositoryImpl
import com.imecatro.demosales.data.sales.datasource.OrdersRoomDao
import com.imecatro.demosales.data.sales.datasource.SalesRoomDao
import com.imecatro.demosales.data.sales.details.DetailsSaleRepositoryImpl
import com.imecatro.demosales.data.sales.list.repository.AllSalesRepositoryImpl
import com.imecatro.demosales.domain.clients.repository.ClientsRepository
import com.imecatro.demosales.domain.products.repository.CategoriesRepository
import com.imecatro.demosales.domain.products.repository.ProductsRepository
import com.imecatro.demosales.domain.sales.add.repository.AddSaleRepository
import com.imecatro.demosales.domain.sales.details.DetailsSaleRepository
import com.imecatro.demosales.domain.sales.list.repository.AllSalesRepository
import com.imecatro.products.data.datasource.CategoriesDao
import com.imecatro.products.data.datasource.ProductsDao
import com.imecatro.products.data.repository.CategoriesRepositoryImpl
import com.imecatro.products.data.repository.ProductsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    fun provideRoomRepositoryImplementation(
        dao: ProductsDao,
        categories: CategoriesDao
    ): ProductsRepository =
        ProductsRepositoryImpl(dao, categories)

    @Provides
    fun providesCategoriesRepoImplementation(categories: CategoriesDao): CategoriesRepository =
        CategoriesRepositoryImpl(categories)

    @Provides
    fun providesSalesListRepository(
        dao: SalesRoomDao
    ): AllSalesRepository =
        AllSalesRepositoryImpl(dao)

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
    ): DetailsSaleRepository = DetailsSaleRepositoryImpl(salesRoomDao, ordersRoomDao)


    @Provides
    @Singleton
    fun providesClientsRepository(
        clientsDao: ClientsDao
    ): ClientsRepository = ClientsRepositoryImpl(clientsDao)
}