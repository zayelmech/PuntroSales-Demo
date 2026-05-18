package com.imecatro.demosales.profile.domain.repository

import com.imecatro.demosales.profile.domain.model.UserProfileDomainModel
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getProfile(): Flow<UserProfileDomainModel>
    suspend fun updateProfile(profile: UserProfileDomainModel)
}
