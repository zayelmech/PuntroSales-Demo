//package com.imecatro.products.ui.di
//
//import com.imecatro.domain.products.repository.ProductsRepository
//import com.imecatro.domain.products.usecases.GetListOfCurrenciesUseCase
//import com.imecatro.domain.products.usecases.GetListOfUnitsUseCase
//import dagger.Provides
//
//    @Provides
//    fun provideGetListOfCurrenciesUseCase(): GetListOfCurrenciesUseCase = GetListOfCurrenciesUseCase()
//
//    @Provides
//    fun provideGetListOfUnitsUseCase(): GetListOfUnitsUseCase = GetListOfUnitsUseCase()
//
//    @Provides
//    fun provideAddViewModel(
//        productsRepository: ProductsRepository,
//        getListOfCurrenciesUseCase: GetListOfCurrenciesUseCase,
//        getListOfUnitsUseCase: GetListOfUnitsUseCase
//    ): AddViewModel {
//        return AddViewModel(
//            productsRepository,
//            getListOfCurrenciesUseCase,
//            getListOfUnitsUseCase
//        )
//    }
//    @Provides
//    fun provideProductViewModel(productsRepository: ProductsRepository): ProductsViewModel {
//        return ProductsViewModel(productsRepository)
//    }
//
//    @Provides
//    fun provideUpdateViewModel(productsRepository: ProductsRepository): UpdateProductViewModel {
//        return UpdateProductViewModel(productsRepository)
//    }
//    @Provides
//    fun provideDetailsViewModel(productsRepository: ProductsRepository): ProductsDetailsViewModel {
//        return ProductsDetailsViewModel(productsRepository)
//    }
//}