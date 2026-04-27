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

/**
 * Hilt module for providing Repository implementations.
 *
 * This module binds the domain-level Repository interfaces to their concrete
 * data-level implementations using Room as the data source.
 */
@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    /**
     * Provides the [ProductsRepository] implementation.
     *
     * @param dao The DAO for products.
     * @param categories The DAO for categories.
     * @return An instance of [ProductsRepositoryImpl].
     */
    @Provides
    fun provideRoomRepositoryImplementation(
        dao: ProductsDao,
        categories: CategoriesDao
    ): ProductsRepository =
        ProductsRepositoryImpl(dao, categories)

    /**
     * Provides the [CategoriesRepository] implementation.
     *
     * @param categories The DAO for categories.
     * @return An instance of [CategoriesRepositoryImpl].
     */
    @Provides
    fun providesCategoriesRepoImplementation(categories: CategoriesDao): CategoriesRepository =
        CategoriesRepositoryImpl(categories)

    /**
     * Provides the [AllSalesRepository] implementation for listing sales.
     *
     * @param dao The DAO for sales.
     * @return An instance of [AllSalesRepositoryImpl].
     */
    @Provides
    fun providesSalesListRepository(
        dao: SalesRoomDao
    ): AllSalesRepository =
        AllSalesRepositoryImpl(dao)

    /**
     * Provides a singleton instance of [AddSaleRepository] for managing new sales.
     *
     * @param dao The DAO for sales.
     * @param ordersRoomDao The DAO for orders.
     * @return An instance of [AddSaleRepositoryImpl].
     */
    @Provides
    @Singleton
    fun providesAddSaleRepoImpl(
        dao: SalesRoomDao,
        ordersRoomDao: OrdersRoomDao
    ): AddSaleRepository = AddSaleRepositoryImpl(dao, ordersRoomDao)


    /**
     * Provides the [DetailsSaleRepository] implementation for viewing sale details.
     *
     * @param salesRoomDao The DAO for sales.
     * @param ordersRoomDao The DAO for orders.
     * @return An instance of [DetailsSaleRepositoryImpl].
     */
    @Provides
    fun providesDetailsSaleRepoImpl(
        salesRoomDao: SalesRoomDao,
        ordersRoomDao: OrdersRoomDao
    ): DetailsSaleRepository = DetailsSaleRepositoryImpl(salesRoomDao, ordersRoomDao)


    /**
     * Provides a singleton instance of [ClientsRepository].
     *
     * @param clientsDao The DAO for clients.
     * @return An instance of [ClientsRepositoryImpl].
     */
    @Provides
    @Singleton
    fun providesClientsRepository(
        clientsDao: ClientsDao
    ): ClientsRepository = ClientsRepositoryImpl(clientsDao)
}