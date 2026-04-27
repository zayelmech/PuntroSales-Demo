package com.imecatro.demosales.di

import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.sales.list.repository.AllSalesRepository
import com.imecatro.demosales.domain.core.files.FileInteractor
import com.imecatro.demosales.domain.sales.list.usecases.ExportSalesReportUseCase
import com.imecatro.demosales.domain.sales.add.repository.AddSaleRepository
import com.imecatro.demosales.domain.sales.add.usecases.AddNewSaleToDatabaseUseCase
import com.imecatro.demosales.domain.sales.add.usecases.AddProductToCartUseCase
import com.imecatro.demosales.domain.sales.add.usecases.CheckoutSaleUseCase
import com.imecatro.demosales.domain.sales.add.usecases.DeleteProductOnCartUseCase
import com.imecatro.demosales.domain.sales.add.usecases.DeleteTicketByIdUseCase
import com.imecatro.demosales.domain.sales.add.usecases.GetCartFlowUseCase
import com.imecatro.demosales.domain.sales.add.usecases.GetMostPopularProductsUseCase
import com.imecatro.demosales.domain.sales.add.usecases.UpdateProductOnCartUseCase
import com.imecatro.demosales.domain.sales.add.usecases.UpdateSaleClientUseCase
import com.imecatro.demosales.domain.sales.add.usecases.UpdateTicketStatusUseCase
import com.imecatro.demosales.domain.sales.details.DetailsSaleRepository
import com.imecatro.demosales.domain.sales.details.GetDetailsOfSaleByIdUseCase
import com.imecatro.demosales.domain.sales.details.UpdateSaleStatusUseCase
import com.imecatro.demosales.domain.sales.list.usecases.ExportProductsFromSaleUseCase
import com.imecatro.demosales.domain.sales.list.usecases.GetAllSalesUseCase
import com.imecatro.demosales.domain.sales.list.usecases.GetSalesMetricsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

/**
 * Hilt module for providing Use Case dependencies related to Sales.
 *
 * This module provides various use cases for listing sales, adding new sales,
 * managing the shopping cart, and exporting sales data.
 */
@Module
@InstallIn(ViewModelComponent::class)
object SalesUseCases {

    /**
     * Provides [ExportSalesReportUseCase].
     */
    @Provides
    fun providesExportSalesReportUseCase(
        allSalesRepository: AllSalesRepository,
        fileInteractor: FileInteractor,
        coroutineDispatcher: CoroutineProvider
    ): ExportSalesReportUseCase =
        ExportSalesReportUseCase(allSalesRepository, fileInteractor, coroutineDispatcher)

    /**
     * Provides [ExportProductsFromSaleUseCase].
     */
    @Provides
    fun providesExportProductsFromSaleUseCase(
        detailsSaleRepository: DetailsSaleRepository,
        fileInteractor: FileInteractor,
        coroutineDispatcher: CoroutineProvider
    ): ExportProductsFromSaleUseCase =
        ExportProductsFromSaleUseCase(detailsSaleRepository, fileInteractor, coroutineDispatcher)

    /**
     * Provides [GetMostPopularProductsUseCase].
     */
    @Provides
    fun providesGetMostPopularProductsUseCase(addSaleRepository: AddSaleRepository): GetMostPopularProductsUseCase {
        return GetMostPopularProductsUseCase(addSaleRepository)
    }

    /**
     * Provides [AddNewSaleToDatabaseUseCase].
     */
    @Provides
    fun providesAddNewSaleToDatabaseUseCase(addSaleRepository: AddSaleRepository): AddNewSaleToDatabaseUseCase {
        return AddNewSaleToDatabaseUseCase(addSaleRepository)
    }

    /**
     * Provides [UpdateTicketStatusUseCase].
     */
    @Provides
    fun providesUpdateTicketStatusUseCase(addSaleRepository: AddSaleRepository): UpdateTicketStatusUseCase {
        return UpdateTicketStatusUseCase(addSaleRepository)
    }

    /**
     * Provides [AddProductToCartUseCase].
     */
    @Provides
    fun provideAddProductToCartUseCase(addSaleRepository: AddSaleRepository): AddProductToCartUseCase =
        AddProductToCartUseCase(addSaleRepository)

    /**
     * Provides [UpdateProductOnCartUseCase].
     */
    @Provides
    fun provideUpdateProductToCartUseCase(addSaleRepository: AddSaleRepository): UpdateProductOnCartUseCase =
        UpdateProductOnCartUseCase(addSaleRepository)

    /**
     * Provides [UpdateSaleClientUseCase].
     */
    @Provides
    fun provideUpdateSaleClientUseCase(addSaleRepository: AddSaleRepository): UpdateSaleClientUseCase =
        UpdateSaleClientUseCase(addSaleRepository)

    /**
     * Provides [GetCartFlowUseCase].
     */
    @Provides
    fun providesGetCartFlowUseCase(addSaleRepository: AddSaleRepository, detailsSaleRepository: DetailsSaleRepository): GetCartFlowUseCase =
        GetCartFlowUseCase(addSaleRepository, detailsSaleRepository)

    /**
     * Provides [GetAllSalesUseCase].
     */
    @Provides
    fun provideGetAllSalesUseCase(
        allSalesRepository: AllSalesRepository
    ) = GetAllSalesUseCase(allSalesRepository)

    /**
     * Provides [CheckoutSaleUseCase].
     */
    @Provides
    fun provideSaveSaleUseCase(
        allSalesRepository: AddSaleRepository
    ) = CheckoutSaleUseCase(allSalesRepository)


    /**
     * Provides [GetDetailsOfSaleByIdUseCase].
     */
    @Provides
    fun providesGetDetailsSaleUseCase(repo: DetailsSaleRepository) =
        GetDetailsOfSaleByIdUseCase(repo)

    /**
     * Provides [DeleteTicketByIdUseCase].
     */
    @Provides
    fun providesDeleteTicketByIdUseCase(repo: DetailsSaleRepository) =
        DeleteTicketByIdUseCase(repo)


    /**
     * Provides [UpdateSaleStatusUseCase].
     */
    @Provides
    fun providesUpdateSaleStatus(repo: DetailsSaleRepository) =
        UpdateSaleStatusUseCase(repo)

    /**
     * Provides [DeleteProductOnCartUseCase].
     */
    @Provides
    fun providesDeleteProductOnCartUseCase(addSaleRepository: AddSaleRepository) =
        DeleteProductOnCartUseCase(addSaleRepository)

    /**
     * Provides [GetSalesMetricsUseCase].
     */
    @Provides
    fun providesGetSalesMetrics(allSalesRepository: AllSalesRepository) =
        GetSalesMetricsUseCase(allSalesRepository)

}