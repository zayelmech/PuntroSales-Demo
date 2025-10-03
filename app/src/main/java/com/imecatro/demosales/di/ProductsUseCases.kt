package com.imecatro.demosales.di

import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.products.repository.CategoriesRepository
import com.imecatro.demosales.domain.products.repository.ProductsRepository
import com.imecatro.demosales.domain.products.usecases.AddCategoryUseCase
import com.imecatro.demosales.domain.products.usecases.DeleteCategoryUseCase
import com.imecatro.demosales.domain.products.usecases.GetAllCategoriesUseCase
import com.imecatro.demosales.domain.products.usecases.UpdateCategoryUseCase
import com.imecatro.demosales.domain.products.usecases.GetProductsLikeUseCase
import com.imecatro.demosales.domain.products.usecases.AddStockUseCase
import com.imecatro.demosales.domain.products.usecases.GetListOfCurrenciesUseCase
import com.imecatro.demosales.domain.products.usecases.GetListOfUnitsUseCase
import com.imecatro.demosales.domain.products.usecases.GetProductDetailsByIdUseCase
import com.imecatro.demosales.domain.products.usecases.RemoveFromStockUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import kotlinx.coroutines.CoroutineDispatcher


@Module
@InstallIn(ViewModelComponent::class)
object ProductsUseCases {

    @Provides
    fun providesGetAllCategoriesUseCase(
        categoriesRepository: CategoriesRepository,
        coroutineDispatcher: CoroutineProvider
    ): GetAllCategoriesUseCase =
        GetAllCategoriesUseCase(categoriesRepository, coroutineDispatcher)

    @Provides
    fun providesAddCategoryUseCase(
        categoriesRepository: CategoriesRepository,
        coroutineDispatcher: CoroutineProvider
    ): AddCategoryUseCase =
        AddCategoryUseCase(categoriesRepository, coroutineDispatcher)


    @Provides
    fun providesUpdateCategoryUseCase(
        categoriesRepository: CategoriesRepository,
        coroutineDispatcher: CoroutineProvider
    ): UpdateCategoryUseCase =
        UpdateCategoryUseCase(categoriesRepository, coroutineDispatcher)

    @Provides
    fun providesDeleteCategoryUseCase(
        categoriesRepository: CategoriesRepository,
        coroutineDispatcher: CoroutineProvider
    ): DeleteCategoryUseCase =
        DeleteCategoryUseCase(categoriesRepository, coroutineDispatcher)


    @Provides
    fun providesAddStockUseCase(productsRepository: ProductsRepository) =
        AddStockUseCase(productsRepository)

    @Provides
    fun providesRemoveFromStockUseCase(productsRepository: ProductsRepository) =
        RemoveFromStockUseCase(productsRepository)

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
    fun providesGetProductDetailsByIdUseCase(
        productsRepository: ProductsRepository,
        ioDispatcher: CoroutineDispatcher
    ): GetProductDetailsByIdUseCase =
        GetProductDetailsByIdUseCase(productsRepository, ioDispatcher)

}