package com.imecatro.demosales.di

import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.sales.list.repository.AllSalesRepository
import com.imecatro.demosales.domain.sales.list.repository.FileInteractor
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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object SalesUseCases {

    @Provides
    fun providesExportSalesReportUseCase(
        allSalesRepository: AllSalesRepository,
        fileInteractor: FileInteractor,
        coroutineDispatcher: CoroutineProvider
    ): ExportSalesReportUseCase =
        ExportSalesReportUseCase(allSalesRepository, fileInteractor, coroutineDispatcher)

    @Provides
    fun providesExportProductsFromSaleUseCase(
        detailsSaleRepository: DetailsSaleRepository,
        fileInteractor: FileInteractor,
        coroutineDispatcher: CoroutineProvider
    ): ExportProductsFromSaleUseCase =
        ExportProductsFromSaleUseCase(detailsSaleRepository, fileInteractor, coroutineDispatcher)

    @Provides
    fun providesGetMostPopularProductsUseCase(addSaleRepository: AddSaleRepository): GetMostPopularProductsUseCase {
        return GetMostPopularProductsUseCase(addSaleRepository)
    }

    @Provides
    fun providesAddNewSaleToDatabaseUseCase(addSaleRepository: AddSaleRepository): AddNewSaleToDatabaseUseCase {
        return AddNewSaleToDatabaseUseCase(addSaleRepository)
    }
    @Provides
    fun providesUpdateTicketStatusUseCase(addSaleRepository: AddSaleRepository): UpdateTicketStatusUseCase {
        return UpdateTicketStatusUseCase(addSaleRepository)
    }

    @Provides
    fun provideAddProductToCartUseCase(addSaleRepository: AddSaleRepository): AddProductToCartUseCase =
        AddProductToCartUseCase(addSaleRepository)

    @Provides
    fun provideUpdateProductToCartUseCase(addSaleRepository: AddSaleRepository): UpdateProductOnCartUseCase =
        UpdateProductOnCartUseCase(addSaleRepository)

    @Provides
    fun provideUpdateSaleClientUseCase(addSaleRepository: AddSaleRepository): UpdateSaleClientUseCase =
        UpdateSaleClientUseCase(addSaleRepository)

    @Provides
    fun providesGetCartFlowUseCase(addSaleRepository: AddSaleRepository, detailsSaleRepository: DetailsSaleRepository): GetCartFlowUseCase =
        GetCartFlowUseCase(addSaleRepository, detailsSaleRepository)

    @Provides
    fun provideGetAllSalesUseCase(
        allSalesRepository: AllSalesRepository
    ) = GetAllSalesUseCase(allSalesRepository)

    @Provides
    fun provideSaveSaleUseCase(
        allSalesRepository: AddSaleRepository
    ) = CheckoutSaleUseCase(allSalesRepository)


    @Provides
    fun providesGetDetailsSaleUseCase(repo: DetailsSaleRepository) =
        GetDetailsOfSaleByIdUseCase(repo)

    @Provides
    fun providesDeleteTicketByIdUseCase(repo: DetailsSaleRepository) =
        DeleteTicketByIdUseCase(repo)


    @Provides
    fun providesUpdateSaleStatus(repo: DetailsSaleRepository) =
        UpdateSaleStatusUseCase(repo)

    @Provides
    fun providesDeleteProductOnCartUseCase(addSaleRepository: AddSaleRepository) =
        DeleteProductOnCartUseCase(addSaleRepository)

}