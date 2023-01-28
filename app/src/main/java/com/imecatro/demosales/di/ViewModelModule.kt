package com.imecatro.demosales.di

import com.imecatro.products.ui.di.ViewModelFactory
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent


@Module(includes = [ViewModelFactory::class])
@InstallIn(ActivityComponent::class)
interface ViewModelModule {

//    @Provides
//    fun provideViewModelProvider(@ViewModelFactoryDsl applicationContext: ViewModelStoreOwner): ViewModelProvider{
//      return  ViewModelProvider(applicationContext)
//    }

//    @Provides
//    fun provideAddViewModel(viewModelProvider: ViewModelProvider) : AddViewModel{
//        return viewModelProvider.get()
//    }


}