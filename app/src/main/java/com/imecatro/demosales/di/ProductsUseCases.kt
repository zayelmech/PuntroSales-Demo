package com.imecatro.demosales.di

import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.products.repository.CategoriesRepository
import com.imecatro.demosales.domain.products.usecases.AddCategoryUseCase
import com.imecatro.demosales.domain.products.usecases.DeleteCategoryUseCase
import com.imecatro.demosales.domain.products.usecases.GetAllCategoriesUseCase
import com.imecatro.demosales.domain.products.usecases.UpdateCategoryUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent


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

}