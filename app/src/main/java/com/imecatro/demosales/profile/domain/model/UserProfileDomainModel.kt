package com.imecatro.demosales.profile.domain.model

data class UserProfileDomainModel(
    val storeName: String,
    val storeLogoUri: String,
    val language: String,
    val currency: String,
    val isDarkTheme: Boolean
) {
    companion object {
        val default = UserProfileDomainModel(
            storeName = "Puntro Sales",
            storeLogoUri = "",
            language = "en",
            currency = "USD",
            isDarkTheme = false
        )
    }
}
