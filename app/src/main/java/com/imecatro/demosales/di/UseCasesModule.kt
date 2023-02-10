package com.imecatro.demosales.di

import com.imecatro.demosales.domain.products.repository.ProductsRepository
import com.imecatro.demosales.domain.products.search.GetProductsLikeUseCase
import com.imecatro.demosales.domain.products.usecases.GetListOfCurrenciesUseCase
import com.imecatro.demosales.domain.products.usecases.GetListOfUnitsUseCase
import com.imecatro.demosales.domain.products.usecases.GetProductDetailsByIdUseCase
import com.imecatro.demosales.domain.sales.add.repository.AddSaleRepository
import com.imecatro.demosales.domain.sales.add.usecases.AddNewSaleToDatabaseUseCase
import com.imecatro.demosales.domain.sales.add.usecases.AddProductToCartUseCase
import com.imecatro.demosales.domain.sales.add.usecases.GetCartFlowUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(ViewModelComponent::class)
class UseCasesModule {
    @Provides
    fun provideGetListOfCurrenciesUseCase(): GetListOfCurrenciesUseCase =
        GetListOfCurrenciesUseCase()

    @Provides
    fun provideGetListOfUnitsUseCase(): GetListOfUnitsUseCase = GetListOfUnitsUseCase()


    @Provides
    fun providesGetProductsLikeUseCase(productsRepository: ProductsRepository): GetProductsLikeUseCase {
        return GetProductsLikeUseCase(productsRepository)
    }

    @Provides
    fun providesAddNewSaleToDatabaseUseCase(addSaleRepository: AddSaleRepository): AddNewSaleToDatabaseUseCase {
        return AddNewSaleToDatabaseUseCase(addSaleRepository)
    }

    @Provides
    fun providesGetProductDetailsByIdUseCase(productsRepository: ProductsRepository, ioDispatcher: CoroutineDispatcher): GetProductDetailsByIdUseCase =
        GetProductDetailsByIdUseCase(productsRepository,ioDispatcher)

    @Provides
    fun provideAddProductToCartUseCase(addSaleRepository: AddSaleRepository) : AddProductToCartUseCase = AddProductToCartUseCase(addSaleRepository)

    @Provides
    fun providesGetCartFlowUseCase(addSaleRepository: AddSaleRepository) : GetCartFlowUseCase = GetCartFlowUseCase(addSaleRepository)
    @Provides
    fun provideDispatcher(): CoroutineDispatcher = Dispatchers.IO
}