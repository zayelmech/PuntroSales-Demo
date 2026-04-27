package com.imecatro.demosales.di

import com.imecatro.demosales.domain.clients.repository.ClientsRepository
import com.imecatro.demosales.domain.clients.usecases.*
import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Hilt module for providing general Use Case dependencies.
 */
@Module
@InstallIn(ViewModelComponent::class)
class UseCasesModule {

    /**
     * Provides a standard I/O Coroutine Dispatcher.
     *
     * @return [Dispatchers.IO]
     */
    @Provides
    fun provideDispatcher(): CoroutineDispatcher = Dispatchers.IO

}

/**
 * Hilt module for providing Use Case dependencies related to Clients.
 */
@Module
@InstallIn(ViewModelComponent::class)
class ClientsFeaturesModule {

    /**
     * Provides [GetAllClientsUseCase].
     */
    @Provides
    fun providesGetListOfClientsUseCase(
        clientsRepository: ClientsRepository,
        appCoroutineDispatcher: CoroutineProvider
    ): GetAllClientsUseCase = GetAllClientsUseCase(clientsRepository, appCoroutineDispatcher)

    /**
     * Provides [FilterClientsUseCase].
     */
    @Provides
    fun providesFilterClientsUseCase(
        clientsRepository: ClientsRepository,
        appCoroutineDispatcher: CoroutineProvider
    ): FilterClientsUseCase = FilterClientsUseCase(clientsRepository, appCoroutineDispatcher)

    /**
     * Provides [AddClientUseCase].
     */
    @Provides
    fun providesAddClientUseCase(
        clientsRepository: ClientsRepository,
        appCoroutineDispatcher: CoroutineProvider
    ): AddClientUseCase = AddClientUseCase(clientsRepository, appCoroutineDispatcher)

    /**
     * Provides [GetClientDetailsByIdUseCase].
     */
    @Provides
    fun providesClientDetailsByIdUseCase(
        clientsRepository: ClientsRepository,
        appCoroutineDispatcher: CoroutineProvider
    ): GetClientDetailsByIdUseCase =
        GetClientDetailsByIdUseCase(clientsRepository, appCoroutineDispatcher)


    /**
     * Provides [DeleteClientByIdUseCase].
     */
    @Provides
    fun providesDeleteClientByIdUseCase(
        clientsRepository: ClientsRepository,
        appCoroutineDispatcher: CoroutineProvider
    ): DeleteClientByIdUseCase = DeleteClientByIdUseCase(clientsRepository, appCoroutineDispatcher)

    /**
     * Provides [UpdateClientUseCase].
     */
    @Provides
    fun providesUpdateClientUseCase(
        clientsRepository: ClientsRepository,
        appCoroutineDispatcher: CoroutineProvider
    ): UpdateClientUseCase = UpdateClientUseCase(clientsRepository, appCoroutineDispatcher)

    /**
     * Provides [SearchClientUseCase].
     */
    @Provides
    fun providesSearchClientUseCase(
        clientsRepository: ClientsRepository,
    ): SearchClientUseCase = SearchClientUseCase(clientsRepository)

    /**
     * Provides [GetClientByPhoneNumberUseCase].
     */
    @Provides
    fun providesGetClientByPhoneNumberUseCase(
        clientsRepository: ClientsRepository,
        appCoroutineDispatcher: CoroutineProvider
    ): GetClientByPhoneNumberUseCase = GetClientByPhoneNumberUseCase(clientsRepository, appCoroutineDispatcher)

    /**
     * Provides [AddPurchaseUseCase].
     */
    @Provides
    fun providesAddPurchaseUseCase(
        clientsRepository: ClientsRepository
    ): AddPurchaseUseCase = AddPurchaseUseCase(clientsRepository)

    /**
     * Provides [GetPurchasesByClientIdUseCase].
     */
    @Provides
    fun providesGetPurchasesByClientIdUseCase(
        clientsRepository: ClientsRepository
    ): GetPurchasesByClientIdUseCase = GetPurchasesByClientIdUseCase(clientsRepository)

    /**
     * Provides [CancelPurchaseByNumberUseCase].
     */
    @Provides
    fun providesCancelPurchaseByNumberUseCase(
        clientsRepository: ClientsRepository,dispatcher: CoroutineProvider
    ): CancelPurchaseByNumberUseCase = CancelPurchaseByNumberUseCase(clientsRepository,dispatcher)

    /**
     * Provides [UpdateFavoriteStatusUseCase].
     */
    @Provides
    fun providesUpdateFavoriteStatusUseCase(
        clientsRepository: ClientsRepository,
        dispatcher: CoroutineProvider
    ): UpdateFavoriteStatusUseCase = UpdateFavoriteStatusUseCase(clientsRepository, dispatcher)
}