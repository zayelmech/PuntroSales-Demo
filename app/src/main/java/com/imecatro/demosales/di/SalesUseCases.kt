package com.imecatro.demosales.di

import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.products.repository.CategoriesRepository
import com.imecatro.demosales.domain.sales.list.repository.AllSalesRepository
import com.imecatro.demosales.domain.sales.list.repository.FileInteractor
import com.imecatro.demosales.domain.sales.list.usecases.ExportSalesReportUseCase
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

}