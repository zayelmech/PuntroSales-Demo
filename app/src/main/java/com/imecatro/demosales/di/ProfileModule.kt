package com.imecatro.demosales.di

import android.content.Context
import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.profile.data.repository.ProfileRepositoryImpl
import com.imecatro.demosales.profile.domain.repository.ProfileRepository
import com.imecatro.demosales.profile.domain.usecases.GetProfileUseCase
import com.imecatro.demosales.profile.domain.usecases.UpdateProfileUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProfileModule {

    @Provides
    @Singleton
    fun provideProfileRepository(@ApplicationContext context: Context): ProfileRepository =
        ProfileRepositoryImpl(context)

    @Provides
    fun provideGetProfileUseCase(
        profileRepository: ProfileRepository,
        coroutineProvider: CoroutineProvider
    ): GetProfileUseCase = GetProfileUseCase(profileRepository, coroutineProvider)

    @Provides
    fun provideUpdateProfileUseCase(
        profileRepository: ProfileRepository,
        coroutineProvider: CoroutineProvider
    ): UpdateProfileUseCase = UpdateProfileUseCase(profileRepository, coroutineProvider)
}
