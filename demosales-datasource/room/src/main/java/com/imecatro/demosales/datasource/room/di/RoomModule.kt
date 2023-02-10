package com.imecatro.demosales.datasource.room.di

import android.app.Application
import com.imecatro.demosales.datasource.room.ProductsDao
import com.imecatro.demosales.datasource.room.ProductsRepositoryImpl
import com.imecatro.demosales.datasource.room.ProductsRoomDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class RoomModule {

    @Provides
    @Singleton
    fun provideAppDatabase( app: Application): ProductsRoomDatabase {
        return  ProductsRoomDatabase.initDatabase(app)
    }

    @Provides
    fun provideLaunchDao(appDatabase: ProductsRoomDatabase): ProductsDao {
        return appDatabase.productsRoomDao()
    }

    @Provides
    fun provideRoomRepositoryImplementation(dao : ProductsDao) : ProductsRepositoryImpl =
        ProductsRepositoryImpl(dao)

}