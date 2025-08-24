package com.imecatro.demosales.datasource.room.di

import android.app.Application
import android.content.Context
import com.imecatro.demosales.data.clients.datasource.ClientsDao
import com.imecatro.demosales.data.sales.datasource.OrdersRoomDao
import com.imecatro.demosales.data.sales.datasource.SalesRoomDao
import com.imecatro.demosales.datasource.room.ProductsRoomDatabase
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
    fun provideAppDatabase(@ApplicationContext app:  Context): ProductsRoomDatabase {
        return ProductsRoomDatabase.initDatabase(app)
    }

    @Provides
    fun providesProductsDao(appDatabase: ProductsRoomDatabase): ProductsDao {
        return appDatabase.productsRoomDao()
    }

    @Provides
    fun providesSalessDao(appDatabase: ProductsRoomDatabase): SalesRoomDao {
        return appDatabase.salesRoomDao()
    }

    @Provides
    fun providesOrdersDao(appDatabase: ProductsRoomDatabase): OrdersRoomDao {
        return appDatabase.ordersRoomDao()
    }

    @Provides
    fun providesClientsDao(appDatabase: ProductsRoomDatabase): ClientsDao {
        return appDatabase.clientsRoomDao()
    }
}