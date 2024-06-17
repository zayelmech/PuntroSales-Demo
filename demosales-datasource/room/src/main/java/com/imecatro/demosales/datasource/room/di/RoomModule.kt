package com.imecatro.demosales.datasource.room.di

import android.app.Application
import com.imecatro.demosales.data.clients.datasource.ClientsDao
import com.imecatro.demosales.data.sales.datasource.OrdersRoomDao
import com.imecatro.demosales.data.sales.datasource.SalesRoomDao
import com.imecatro.demosales.datasource.room.ProductsRoomDatabase
import com.imecatro.products.data.datasource.ProductsDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class RoomModule {

    @Provides
    @Singleton
    fun provideAppDatabase(app: Application): ProductsRoomDatabase {
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