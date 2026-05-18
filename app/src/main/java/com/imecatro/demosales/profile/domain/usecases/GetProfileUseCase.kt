package com.imecatro.demosales.profile.domain.usecases

import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.core.architecture.usecase.BackgroundUseCase
import com.imecatro.demosales.profile.domain.model.UserProfileDomainModel
import com.imecatro.demosales.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow

class GetProfileUseCase(
    private val profileRepository: ProfileRepository,
    coroutineProvider: CoroutineProvider
) : BackgroundUseCase<Unit, Flow<UserProfileDomainModel>>(coroutineProvider) {

    operator fun invoke(): Flow<UserProfileDomainModel> = profileRepository.getProfile()

    override suspend fun doInBackground(input: Unit): Flow<UserProfileDomainModel> {
        return profileRepository.getProfile()
    }
}
