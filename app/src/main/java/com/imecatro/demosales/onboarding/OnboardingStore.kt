package com.imecatro.demosales.onboarding

import android.content.Context
import android.content.SharedPreferences
import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

interface OnboardingPersistence {
    fun read(): OnboardingProgress
    fun write(progress: OnboardingProgress)
}

class PreferencesOnboardingPersistence internal constructor(
    private val preferences: SharedPreferences,
    private val legacyProfile: SharedPreferences
) : OnboardingPersistence {
    @Inject constructor(@ApplicationContext context: Context) : this(
        context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE),
        context.getSharedPreferences("user_profile_prefs", Context.MODE_PRIVATE)
    )

    override fun read(): OnboardingProgress {
        val keys = preferences.getStringSet("steps", emptySet()).orEmpty()
        val steps = OnboardingStep.entries.filter { it.key in keys }.toSet()
        val state = OnboardingProgress(
            presentedVersion = preferences.getInt("presented_version", 0),
            steps = steps,
            productCount = preferences.getInt("product_count", 0),
            completed = preferences.getBoolean("completed", false),
            completionAcknowledged = preferences.getBoolean("completion_acknowledged", false)
        )
        // Old versions had no confirmation flag. Only non-default saved business
        // information is evidence; changing currency/theme also saved all defaults.
        return if (isConfirmedLegacyBusiness(legacyProfile.getString("store_name", null))) {
            state.complete(OnboardingStep.BUSINESS)
        } else state
    }

    override fun write(progress: OnboardingProgress) {
        check(preferences.edit()
            .putInt("presented_version", progress.presentedVersion)
            .putStringSet("steps", progress.steps.map { it.key }.toSet())
            .putInt("product_count", progress.productCount)
            .putBoolean("completed", progress.completed)
            .putBoolean("completion_acknowledged", progress.completionAcknowledged)
            .commit()) { "Could not save onboarding progress" }
    }
}

@Singleton
class OnboardingStore @Inject constructor(
    private val persistence: OnboardingPersistence,
    private val dispatchers: CoroutineProvider
) {
    private val mutex = Mutex()
    private val mutableProgress = MutableStateFlow(persistence.read())
    val progress = mutableProgress.asStateFlow()

    private suspend fun update(transform: (OnboardingProgress) -> OnboardingProgress) =
        withContext(dispatchers.io) {
            mutex.withLock {
                val updated = transform(mutableProgress.value)
                persistence.write(updated)
                mutableProgress.value = updated
            }
        }

    /** Claim once, before rendering. Retained ViewModel owns visibility across rotation. */
    suspend fun claimPresentation(): Boolean {
        var claimed = false
        update {
            claimed = it.needsPresentation
            if (claimed) it.copy(presentedVersion = ONBOARDING_VERSION) else it
        }
        return claimed
    }

    suspend fun complete(step: OnboardingStep) = update { it.complete(step) }
    suspend fun productsSaved(count: Int) = update { it.withProducts(count) }
    suspend fun acknowledgeCompletion() = update { it.copy(completionAcknowledged = true) }
}
