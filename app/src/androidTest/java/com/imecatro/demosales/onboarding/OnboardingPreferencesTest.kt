package com.imecatro.demosales.onboarding

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Test

class OnboardingPreferencesTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val prefs = context.getSharedPreferences("test_onboarding", Context.MODE_PRIVATE)
    private val legacy = context.getSharedPreferences("test_legacy_profile", Context.MODE_PRIVATE)
    private val dispatchers = object : CoroutineProvider {
        override val io = Dispatchers.IO
        override val main = Dispatchers.Main
    }
    private fun store() = OnboardingStore(PreferencesOnboardingPersistence(prefs, legacy), dispatchers)

    @After fun clear() {
        prefs.edit().clear().commit()
        legacy.edit().clear().commit()
    }

    @Test fun cleanInstallPersistsVersionSeparatelyFromSteps() = runBlocking {
        clear()
        assertTrue(store().claimPresentation())
        assertFalse(store().claimPresentation())
        assertTrue(store().progress.value.steps.isEmpty())
        assertFalse(store().progress.value.completed)
    }

    @Test fun updateFromOldPreferencesRecognizesBusinessButStillShowsIntroduction() = runBlocking {
        clear()
        legacy.edit().putString("store_name", "Mercado Luna").putString("currency", "MXN").commit()
        val upgraded = store()
        assertTrue(upgraded.claimPresentation())
        assertTrue(OnboardingStep.BUSINESS in upgraded.progress.value.steps)
        assertFalse(store().claimPresentation())
    }

    @Test fun changingThemeInOldAppDoesNotCountAsBusinessSetup() = runBlocking {
        clear()
        legacy.edit().putString("store_name", "Puntro Sales").putBoolean("dark_theme", true).commit()
        assertFalse(OnboardingStep.BUSINESS in store().progress.value.steps)
    }

    @Test fun allMilestonesAndCompletionSurviveFreshStore() = runBlocking {
        clear()
        val firstSession = store()
        firstSession.productsSaved(3)
        assertEquals(3, store().progress.value.productCount)
        OnboardingStep.entries.forEach { firstSession.complete(it) }
        assertTrue(store().progress.value.completed)
        assertTrue(store().progress.value.needsPresentation)
        firstSession.claimPresentation()
        firstSession.acknowledgeCompletion()
        assertFalse(store().progress.value.needsPresentation)
        assertTrue(store().progress.value.completionAcknowledged)
    }
}
