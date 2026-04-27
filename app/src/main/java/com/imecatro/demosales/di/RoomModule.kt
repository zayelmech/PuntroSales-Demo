package com.imecatro.demosales.di

import android.content.Context
import com.imecatro.demosales.data.clients.datasource.ClientsDao
import com.imecatro.demosales.data.sales.datasource.OrdersRoomDao
import com.imecatro.demosales.data.sales.datasource.SalesRoomDao
import com.imecatro.demosales.datasource.AppRoomDatabase
import com.imecatro.products.data.datasource.CategoriesDao
import com.imecatro.products.data.datasource.ProductsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing Room database and DAO dependencies.
 *
 * This module is responsible for initializing the [AppRoomDatabase] and providing
 * specific DAO instances to the rest of the application.
 */
@Module
@InstallIn(SingletonComponent::class)
class RoomModule {

    /**
     * Provides a singleton instance of [AppRoomDatabase].
     *
     * @param app The application context.
     * @return The initialized [AppRoomDatabase] instance.
     */
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext app: Context): AppRoomDatabase {
        return AppRoomDatabase.Companion.initDatabase(app)
    }

    /**
     * Provides the [ProductsDao] instance from the database.
     *
     * @param appDatabase The application database.
     * @return The [ProductsDao] instance.
     */
    @Provides
    fun providesProductsDao(appDatabase: AppRoomDatabase): ProductsDao {
        return appDatabase.productsRoomDao()
    }

    /**
     * Provides the [CategoriesDao] instance from the database.
     *
     * @param appDatabase The application database.
     * @return The [CategoriesDao] instance.
     */
    @Provides
    fun providesCategoriesDao(appDatabase: AppRoomDatabase): CategoriesDao {
        return appDatabase.categoriesRoomDao()
    }

    /**
     * Provides the [SalesRoomDao] instance from the database.
     *
     * @param appDatabase The application database.
     * @return The [SalesRoomDao] instance.
     */
    @Provides
    fun providesSalessDao(appDatabase: AppRoomDatabase): SalesRoomDao {
        return appDatabase.salesRoomDao()
    }

    /**
     * Provides the [OrdersRoomDao] instance from the database.
     *
     * @param appDatabase The application database.
     * @return The [OrdersRoomDao] instance.
     */
    @Provides
    fun providesOrdersDao(appDatabase: AppRoomDatabase): OrdersRoomDao {
        return appDatabase.ordersRoomDao()
    }

    /**
     * Provides the [ClientsDao] instance from the database.
     *
     * @param appDatabase The application database.
     * @return The [ClientsDao] instance.
     */
    @Provides
    fun providesClientsDao(appDatabase: AppRoomDatabase): ClientsDao {
        return appDatabase.clientsRoomDao()
    }
}