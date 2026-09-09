package com.imecatro.demosales.domain.core.profile.repository

import com.imecatro.demosales.domain.core.profile.model.UserProfileDomainModel
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getProfile(): Flow<UserProfileDomainModel>
    suspend fun updateProfile(profile: UserProfileDomainModel)
}
