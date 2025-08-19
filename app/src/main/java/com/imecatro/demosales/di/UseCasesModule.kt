package com.imecatro.demosales.di

import com.imecatro.demosales.domain.clients.repository.ClientsRepository
import com.imecatro.demosales.domain.clients.usecases.AddClientUseCase
import com.imecatro.demosales.domain.clients.usecases.DeleteClientByIdUseCase
import com.imecatro.demosales.domain.clients.usecases.GetAllClientsUseCase
import com.imecatro.demosales.domain.clients.usecases.GetClientDetailsByIdUseCase
import com.imecatro.demosales.domain.clients.usecases.SearchClientUseCase
import com.imecatro.demosales.domain.clients.usecases.UpdateClientUseCase
import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.products.repository.ProductsRepository
import com.imecatro.demosales.domain.products.search.GetProductsLikeUseCase
import com.imecatro.demosales.domain.products.usecases.AddStockUseCase
import com.imecatro.demosales.domain.products.usecases.GetListOfCurrenciesUseCase
import com.imecatro.demosales.domain.products.usecases.GetListOfUnitsUseCase
import com.imecatro.demosales.domain.products.usecases.GetProductDetailsByIdUseCase
import com.imecatro.demosales.domain.products.usecases.RemoveFromStockUseCase
import com.imecatro.demosales.domain.sales.add.repository.AddSaleRepository
import com.imecatro.demosales.domain.sales.add.usecases.AddNewSaleToDatabaseUseCase
import com.imecatro.demosales.domain.sales.add.usecases.AddProductToCartUseCase
import com.imecatro.demosales.domain.sales.add.usecases.DeleteProductOnCartUseCase
import com.imecatro.demosales.domain.sales.add.usecases.GetCartFlowUseCase
import com.imecatro.demosales.domain.sales.add.usecases.GetMostPopularProductsUseCase
import com.imecatro.demosales.domain.sales.add.usecases.CheckoutSaleUseCase
import com.imecatro.demosales.domain.sales.add.usecases.UpdateProductOnCartUseCase
import com.imecatro.demosales.domain.sales.add.usecases.DeleteTicketByIdUseCase
import com.imecatro.demosales.domain.sales.details.DetailsSaleRepository
import com.imecatro.demosales.domain.sales.details.GetDetailsOfSaleByIdUseCase
import com.imecatro.demosales.domain.sales.details.UpdateSaleStatusUseCase
import com.imecatro.demosales.domain.sales.list.repository.AllSalesRepository
import com.imecatro.demosales.domain.sales.list.usecases.GetAllSalesUseCase
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
    fun providesGetMostPopularProductsUseCase(addSaleRepository: AddSaleRepository): GetMostPopularProductsUseCase {
        return GetMostPopularProductsUseCase(addSaleRepository)
    }

    @Provides
    fun providesAddNewSaleToDatabaseUseCase(addSaleRepository: AddSaleRepository): AddNewSaleToDatabaseUseCase {
        return AddNewSaleToDatabaseUseCase(addSaleRepository)
    }

    @Provides
    fun providesGetProductDetailsByIdUseCase(
        productsRepository: ProductsRepository,
        ioDispatcher: CoroutineDispatcher
    ): GetProductDetailsByIdUseCase =
        GetProductDetailsByIdUseCase(productsRepository, ioDispatcher)

    @Provides
    fun provideAddProductToCartUseCase(addSaleRepository: AddSaleRepository): AddProductToCartUseCase =
        AddProductToCartUseCase(addSaleRepository)

    @Provides
    fun provideUpdateProductToCartUseCase(addSaleRepository: AddSaleRepository): UpdateProductOnCartUseCase =
        UpdateProductOnCartUseCase(addSaleRepository)

    @Provides
    fun providesGetCartFlowUseCase(addSaleRepository: AddSaleRepository): GetCartFlowUseCase =
        GetCartFlowUseCase(addSaleRepository)

    @Provides
    fun provideDispatcher(): CoroutineDispatcher = Dispatchers.IO

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

@Module
@InstallIn(ViewModelComponent::class)
class ClientsFeaturesModule {

    @Provides
    fun providesGetListOfClientsUseCase(
        clientsRepository: ClientsRepository,
        appCoroutineDispatcher: CoroutineProvider
    ): GetAllClientsUseCase = GetAllClientsUseCase(clientsRepository, appCoroutineDispatcher)

    @Provides
    fun providesAddClientUseCase(
        clientsRepository: ClientsRepository,
        appCoroutineDispatcher: CoroutineProvider
    ): AddClientUseCase = AddClientUseCase(clientsRepository, appCoroutineDispatcher)

    @Provides
    fun providesClientDetailsByIdUseCase(
        clientsRepository: ClientsRepository,
        appCoroutineDispatcher: CoroutineProvider
    ): GetClientDetailsByIdUseCase =
        GetClientDetailsByIdUseCase(clientsRepository, appCoroutineDispatcher)


    @Provides
    fun providesDeleteClientByIdUseCase(
        clientsRepository: ClientsRepository,
        appCoroutineDispatcher: CoroutineProvider
    ): DeleteClientByIdUseCase = DeleteClientByIdUseCase(clientsRepository, appCoroutineDispatcher)

    @Provides
    fun providesUpdateClientUseCase(
        clientsRepository: ClientsRepository,
        appCoroutineDispatcher: CoroutineProvider
    ): UpdateClientUseCase = UpdateClientUseCase(clientsRepository, appCoroutineDispatcher)

    @Provides
    fun providesSearchClientUseCase(
        clientsRepository: ClientsRepository,
    ): SearchClientUseCase = SearchClientUseCase(clientsRepository)
}