//package com.imecatro.products.ui.di
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import com.imecatro.domain.products.usecases.GetListOfCurrenciesUseCase
//import com.imecatro.domain.products.usecases.GetListOfUnitsUseCase
//import com.imecatro.products.ui.add.viewmodel.AddViewModel
//import com.imecatro.products.ui.details.viewmodels.ProductsDetailsViewModel
//import com.imecatro.products.ui.list.viewmodels.ProductsViewModel
//import com.imecatro.products.ui.update.viewmodel.UpdateProductViewModel
//import dagger.Binds
//import dagger.Module
//import dagger.Provides
//import dagger.multibindings.IntoMap
//
//
//@Module
//abstract class ViewModelFactoryModule {
////    @Provides
////    fun provideGetListOfCurrenciesUseCase(): GetListOfCurrenciesUseCase =
////        GetListOfCurrenciesUseCase()
////
////    @Provides
////    fun provideGetListOfUnitsUseCase(): GetListOfUnitsUseCase = GetListOfUnitsUseCase()
//
//    @Binds
//    @IntoMap
//    @ViewModelKey(UpdateProductViewModel::class)
//    internal abstract fun bindAUpdateProductViewModelFactory(updateProductViewModelFactory: UpdateProductViewModel): ViewModel
//
//    @Binds
//    @IntoMap
//    @ViewModelKey(ProductsViewModel::class)
//    internal abstract fun bindsAddProductsViewModelFactory(productViewModelFactory: ProductsViewModel): ViewModel
//
//    @Binds
//    @IntoMap
//    @ViewModelKey(ProductsDetailsViewModel::class)
//    internal abstract fun bindsProductsDetailsViewModelFactory(productsDetailsViewModelFactory: ProductsDetailsViewModel): ViewModel
//
//    @Binds
//    @IntoMap
//    @ViewModelKey(AddViewModel::class)
//    internal abstract fun bindsProductsViewModel(addProductsViewModelFactory: AddViewModel): ViewModel
//
//    @Binds
//    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
//
////    @Provides
////    fun providesAddViewModel(
////        productsRepository: ProductsRepository,
////        getListOfCurrenciesUseCase: GetListOfCurrenciesUseCase,
////        getListOfUnitsUseCase: GetListOfUnitsUseCase
////    ): AddViewModel {
////        return AddViewModel(
////            productsRepository,
////            getListOfCurrenciesUseCase,
////            getListOfUnitsUseCase
////        )
////    }
////    @Provides
////    fun provideProductViewModelFactory(productsRepository: ProductsRepository): ProductsViewModelFactory {
////        return ProductsViewModelFactory(productsRepository)
////    }
////
////    @Provides
////    fun provideUpdateViewModelFactory(productsRepository: ProductsRepository): UpdateProductViewModelFactory {
////        return UpdateProductViewModelFactory(productsRepository)
////    }
////    @Provides
////    fun provideDetailsViewModelFactory(productsRepository: ProductsRepository): ProductsDetailsViewModelFactory {
////        return ProductsDetailsViewModelFactory(productsRepository)
////    }
//
//}