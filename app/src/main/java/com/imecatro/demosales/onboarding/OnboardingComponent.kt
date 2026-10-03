package com.imecatro.demosales.onboarding

import androidx.compose.runtime.Composable
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

/** App-owned rendering contract. Passed to the generic inventory slot at composition time. */
interface OnboardingComponent {
    @Composable
    fun Banner(progress: OnboardingProgress, onOpenStep: (OnboardingStep) -> Unit, onChecklist: () -> Unit)
}

class MaterialOnboardingComponent @Inject constructor() : OnboardingComponent {
    @Composable
    override fun Banner(progress: OnboardingProgress, onOpenStep: (OnboardingStep) -> Unit, onChecklist: () -> Unit) {
        OnboardingBanner(progress, onOpenStep, onChecklist)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class OnboardingModule {
    @Binds abstract fun component(impl: MaterialOnboardingComponent): OnboardingComponent
    @Binds abstract fun persistence(impl: PreferencesOnboardingPersistence): OnboardingPersistence
}
