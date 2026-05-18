package com.imecatro.demosales.profile.data.repository

import android.content.Context
import androidx.core.content.edit
import com.imecatro.demosales.profile.domain.model.UserProfileDomainModel
import com.imecatro.demosales.profile.domain.repository.ProfileRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ProfileRepository {

    private val sharedPreferences = context.getSharedPreferences("user_profile_prefs", Context.MODE_PRIVATE)

    override fun getProfile(): Flow<UserProfileDomainModel> = callbackFlow {
        val listener = { _: android.content.SharedPreferences, key: String? ->
            if (key == null || key in PROFILE_KEYS) {
                trySend(readProfile())
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        trySend(readProfile())
        awaitClose { sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener) }
    }.onStart { emit(readProfile()) }

    private fun readProfile() = UserProfileDomainModel(
        storeName = sharedPreferences.getString(KEY_STORE_NAME, "Puntro Sales Demo") ?: "Puntro Sales Demo",
        storeLogoUri = sharedPreferences.getString(KEY_STORE_LOGO, "") ?: "",
        language = sharedPreferences.getString(KEY_LANGUAGE, "English") ?: "English",
        currency = sharedPreferences.getString(KEY_CURRENCY, "USD") ?: "USD",
        isDarkTheme = sharedPreferences.getBoolean(KEY_DARK_THEME, false)
    )

    override suspend fun updateProfile(profile: UserProfileDomainModel) {
        sharedPreferences.edit {
            putString(KEY_STORE_NAME, profile.storeName)
            putString(KEY_STORE_LOGO, profile.storeLogoUri)
            putString(KEY_LANGUAGE, profile.language)
            putString(KEY_CURRENCY, profile.currency)
            putBoolean(KEY_DARK_THEME, profile.isDarkTheme)
        }
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
