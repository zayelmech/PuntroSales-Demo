package com.imecatro.demosales.di

import com.imecatro.demosales.datasource.room.di.RoomModule
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module(includes = [RoomModule::class])
@InstallIn(SingletonComponent::class)
interface CoreModule