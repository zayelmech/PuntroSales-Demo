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

@Module
@InstallIn(SingletonComponent::class)
class RoomModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext app: Context): AppRoomDatabase {
        return AppRoomDatabase.Companion.initDatabase(app)
    }

    @Provides
    fun providesProductsDao(appDatabase: AppRoomDatabase): ProductsDao {
        return appDatabase.productsRoomDao()
    }

    @Provides
    fun providesCategoriesDao(appDatabase: AppRoomDatabase): CategoriesDao {
        return appDatabase.categoriesRoomDao()
    }

    @Provides
    fun providesSalessDao(appDatabase: AppRoomDatabase): SalesRoomDao {
        return appDatabase.salesRoomDao()
    }

    @Provides
    fun providesOrdersDao(appDatabase: AppRoomDatabase): OrdersRoomDao {
        return appDatabase.ordersRoomDao()
    }

    @Provides
    fun providesClientsDao(appDatabase: AppRoomDatabase): ClientsDao {
        return appDatabase.clientsRoomDao()
    }
}