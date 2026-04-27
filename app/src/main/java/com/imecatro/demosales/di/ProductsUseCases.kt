package com.imecatro.demosales.di

import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.core.files.FileInteractor
import com.imecatro.demosales.domain.products.repository.CategoriesRepository
import com.imecatro.demosales.domain.products.repository.ProductsRepository
import com.imecatro.demosales.domain.products.usecases.AddCategoryUseCase
import com.imecatro.demosales.domain.products.usecases.DeleteCategoryUseCase
import com.imecatro.demosales.domain.products.usecases.GetAllCategoriesUseCase
import com.imecatro.demosales.domain.products.usecases.UpdateCategoryUseCase
import com.imecatro.demosales.domain.products.usecases.GetProductsLikeUseCase
import com.imecatro.demosales.domain.products.usecases.AddStockUseCase
import com.imecatro.demosales.domain.products.usecases.ExportProductsCsvUseCase
import com.imecatro.demosales.domain.products.usecases.ExportStockHistoryCsvUseCase
import com.imecatro.demosales.domain.products.usecases.GetListOfCurrenciesUseCase
import com.imecatro.demosales.domain.products.usecases.GetListOfUnitsUseCase
import com.imecatro.demosales.domain.products.usecases.GetProductDetailsByIdUseCase
import com.imecatro.demosales.domain.products.usecases.RemoveFromStockUseCase
import com.imecatro.demosales.domain.products.usecases.SearchProductByBarcode
import com.imecatro.demosales.domain.sales.details.GetDetailsOfSaleByIdUseCase
import com.imecatro.demosales.domain.sales.list.repository.AllSalesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import kotlinx.coroutines.CoroutineDispatcher


/**
 * Hilt module for providing Use Case dependencies related to Products and Categories.
 */
@Module
@InstallIn(ViewModelComponent::class)
object ProductsUseCases {

    /**
     * Provides [GetAllCategoriesUseCase].
     */
    @Provides
    fun providesGetAllCategoriesUseCase(
        categoriesRepository: CategoriesRepository,
        coroutineDispatcher: CoroutineProvider
    ): GetAllCategoriesUseCase =
        GetAllCategoriesUseCase(categoriesRepository, coroutineDispatcher)

    /**
     * Provides [AddCategoryUseCase].
     */
    @Provides
    fun providesAddCategoryUseCase(
        categoriesRepository: CategoriesRepository,
        coroutineDispatcher: CoroutineProvider
    ): AddCategoryUseCase =
        AddCategoryUseCase(categoriesRepository, coroutineDispatcher)


    /**
     * Provides [UpdateCategoryUseCase].
     */
    @Provides
    fun providesUpdateCategoryUseCase(
        categoriesRepository: CategoriesRepository,
        coroutineDispatcher: CoroutineProvider
    ): UpdateCategoryUseCase =
        UpdateCategoryUseCase(categoriesRepository, coroutineDispatcher)

    /**
     * Provides [DeleteCategoryUseCase].
     */
    @Provides
    fun providesDeleteCategoryUseCase(
        categoriesRepository: CategoriesRepository,
        coroutineDispatcher: CoroutineProvider
    ): DeleteCategoryUseCase =
        DeleteCategoryUseCase(categoriesRepository, coroutineDispatcher)


    /**
     * Provides [AddStockUseCase].
     */
    @Provides
    fun providesAddStockUseCase(productsRepository: ProductsRepository) =
        AddStockUseCase(productsRepository)

    /**
     * Provides [RemoveFromStockUseCase].
     */
    @Provides
    fun providesRemoveFromStockUseCase(productsRepository: ProductsRepository) =
        RemoveFromStockUseCase(productsRepository)

    /**
     * Provides [GetListOfCurrenciesUseCase].
     */
    @Provides
    fun provideGetListOfCurrenciesUseCase(): GetListOfCurrenciesUseCase =
        GetListOfCurrenciesUseCase()

    /**
     * Provides [GetListOfUnitsUseCase].
     */
    @Provides
    fun provideGetListOfUnitsUseCase(): GetListOfUnitsUseCase = GetListOfUnitsUseCase()


    /**
     * Provides [GetProductsLikeUseCase].
     */
    @Provides
    fun providesGetProductsLikeUseCase(productsRepository: ProductsRepository): GetProductsLikeUseCase {
        return GetProductsLikeUseCase(productsRepository)
    }

    /**
     * Provides [SearchProductByBarcode].
     */
    @Provides
    fun providesSearchProductByBarcode(productsRepository: ProductsRepository,ioDispatcher: CoroutineProvider) : SearchProductByBarcode {
     return SearchProductByBarcode(productsRepository, ioDispatcher)

    }


    /**
     * Provides [GetProductDetailsByIdUseCase].
     */
    @Provides
    fun providesGetProductDetailsByIdUseCase(
        productsRepository: ProductsRepository,
        ioDispatcher: CoroutineDispatcher
    ): GetProductDetailsByIdUseCase =
        GetProductDetailsByIdUseCase(productsRepository, ioDispatcher)


    /**
     * Provides [ExportProductsCsvUseCase].
     */
    @Provides
    fun providesExportProductsCsvUseCase(
        productsRepository: ProductsRepository,
        fileInteractor: FileInteractor,
        coroutineDispatcher: CoroutineProvider
    ): ExportProductsCsvUseCase =
        ExportProductsCsvUseCase(productsRepository, fileInteractor, coroutineDispatcher)


    /**
     * Provides [ExportStockHistoryCsvUseCase].
     */
    @Provides
    fun providesExportStockHistoryCsvUseCase(
        productsRepository: ProductsRepository,
        fileInteractor: FileInteractor,
        coroutineDispatcher: CoroutineProvider
    ): ExportStockHistoryCsvUseCase =
        ExportStockHistoryCsvUseCase(productsRepository, fileInteractor, coroutineDispatcher)
}