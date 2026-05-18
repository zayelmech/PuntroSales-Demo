package com.imecatro.demosales.profile.domain.usecases

import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.core.architecture.usecase.BackgroundUseCase
import com.imecatro.demosales.profile.domain.model.UserProfileDomainModel
import com.imecatro.demosales.profile.domain.repository.ProfileRepository

class UpdateProfileUseCase(
    private val profileRepository: ProfileRepository,
    coroutineProvider: CoroutineProvider
) : BackgroundUseCase<UserProfileDomainModel, Unit>(coroutineProvider) {

    override suspend fun doInBackground(input: UserProfileDomainModel) {
        profileRepository.updateProfile(input)
    }
}
