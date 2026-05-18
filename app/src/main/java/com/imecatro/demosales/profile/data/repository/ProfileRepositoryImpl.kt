package com.imecatro.demosales.profile.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.imecatro.demosales.profile.domain.model.UserProfileDomainModel
import com.imecatro.demosales.profile.domain.repository.ProfileRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Currency
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ProfileRepository {

    private val sharedPreferences = context.getSharedPreferences("user_profile_prefs", Context.MODE_PRIVATE)

    private val _profileState = MutableStateFlow(readProfile())

    // Keep a strong reference to the listener to prevent GC
    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == null || key in PROFILE_KEYS) {
            _profileState.value = readProfile()
        }
    }

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun getProfile(): Flow<UserProfileDomainModel> = _profileState.asStateFlow()

    private fun getDefaultCurrency(): String {
        return try {
            Currency.getInstance(Locale.getDefault()).currencyCode
        } catch (e: Exception) {
            "USD"
        }
    }

    private fun readProfile() = UserProfileDomainModel(
        storeName = sharedPreferences.getString(KEY_STORE_NAME, "Puntro Sales") ?: "Puntro Sales",
        storeLogoUri = sharedPreferences.getString(KEY_STORE_LOGO, "") ?: "",
        language = sharedPreferences.getString(KEY_LANGUAGE, Locale.getDefault().language) ?: "en",
        currency = sharedPreferences.getString(KEY_CURRENCY, getDefaultCurrency()) ?: "USD",
        isDarkTheme = sharedPreferences.getBoolean(KEY_DARK_THEME, false)
    )

    override suspend fun updateProfile(profile: UserProfileDomainModel) {
        sharedPreferences.edit(commit = true) {
            putString(KEY_STORE_NAME, profile.storeName)
            putString(KEY_STORE_LOGO, profile.storeLogoUri)
            putString(KEY_LANGUAGE, profile.language)
            putString(KEY_CURRENCY, profile.currency)
            putBoolean(KEY_DARK_THEME, profile.isDarkTheme)
        }
        // No need to manually update _profileState, the listener will do it.
    }

    companion object {
        private const val KEY_STORE_NAME = "store_name"
        private const val KEY_STORE_LOGO = "store_logo"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_CURRENCY = "currency"
        private const val KEY_DARK_THEME = "dark_theme"

        private val PROFILE_KEYS = setOf(KEY_STORE_NAME, KEY_STORE_LOGO, KEY_LANGUAGE, KEY_CURRENCY, KEY_DARK_THEME)
    }
}
