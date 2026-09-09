package com.imecatro.demosales.domain.core.profile.usecases

import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.core.architecture.usecase.BackgroundUseCase
import com.imecatro.demosales.domain.core.profile.model.UserProfileDomainModel
import com.imecatro.demosales.domain.core.profile.repository.ProfileRepository
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
