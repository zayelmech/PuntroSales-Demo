package com.imecatro.demosales.di

import com.imecatro.products.data.repository.ProductsRepositoryImpl
import com.imecatro.demosales.domain.products.repository.ProductsRepository
import com.imecatro.demosales.domain.sales.add.repository.AddSaleDummyRepoImpl
import com.imecatro.demosales.domain.sales.add.repository.AddSaleRepository
import com.imecatro.demosales.domain.sales.list.repository.AllSalesRepository
import com.imecatro.demosales.domain.sales.list.repository.AllSalesRepositoryDummy
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindRepositoryImpl(productsRepositoryImpl: ProductsRepositoryImpl): ProductsRepository

    @Binds
    abstract fun bindAddSaleRepositoryImpl(addSaleDummyRepoImpl : AddSaleDummyRepoImpl) : AddSaleRepository
}