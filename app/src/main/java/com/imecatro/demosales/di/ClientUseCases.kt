package com.imecatro.demosales.di

import com.imecatro.demosales.domain.clients.repository.ClientsRepository
import com.imecatro.demosales.domain.clients.usecases.AddClientUseCase
import com.imecatro.demosales.domain.clients.usecases.DeleteClientByIdUseCase
import com.imecatro.demosales.domain.clients.usecases.FilterClientsUseCase
import com.imecatro.demosales.domain.clients.usecases.GetAllClientsUseCase
import com.imecatro.demosales.domain.clients.usecases.GetClientDetailsByIdUseCase
import com.imecatro.demosales.domain.clients.usecases.SearchClientUseCase
import com.imecatro.demosales.domain.clients.usecases.UpdateClientUseCase
import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
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
    fun provideDispatcher(): CoroutineDispatcher = Dispatchers.IO

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
    fun providesFilterClientsUseCase(
        clientsRepository: ClientsRepository,
        appCoroutineDispatcher: CoroutineProvider
    ): FilterClientsUseCase = FilterClientsUseCase(clientsRepository, appCoroutineDispatcher)

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