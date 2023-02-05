package com.imecatro.demosales.di

import com.imecatro.demosales.datasource.room.ProductsRepositoryImpl
import com.imecatro.demosales.domain.products.products.repository.ProductsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindRepositoryImpl(productsRepositoryImpl: ProductsRepositoryImpl): ProductsRepository


}