package com.imecatro.demosales.di

import com.imecatro.demosales.datasource.room.di.RoomModule
import com.imecatro.demosales.domain.sales.add.repository.AddSaleDummyRepoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module(includes = [RoomModule::class])
@InstallIn(SingletonComponent::class)
interface CoreModule

@Module
@InstallIn(SingletonComponent::class)
class FakeRepoImpl(){
    @Provides
    fun provideAddSaleDummyRepoImpl(): AddSaleDummyRepoImpl = AddSaleDummyRepoImpl()
}
