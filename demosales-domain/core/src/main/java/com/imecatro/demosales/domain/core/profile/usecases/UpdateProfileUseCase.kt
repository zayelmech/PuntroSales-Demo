package com.imecatro.demosales.domain.core.profile.usecases

import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.core.architecture.usecase.BackgroundUseCase
import com.imecatro.demosales.domain.core.profile.model.UserProfileDomainModel
import com.imecatro.demosales.domain.core.profile.repository.ProfileRepository

class UpdateProfileUseCase(
    private val profileRepository: ProfileRepository,
    coroutineProvider: CoroutineProvider
) : BackgroundUseCase<UserProfileDomainModel, Unit>(coroutineProvider) {

    override suspend fun doInBackground(input: UserProfileDomainModel) {
        profileRepository.updateProfile(input)
    }
}
