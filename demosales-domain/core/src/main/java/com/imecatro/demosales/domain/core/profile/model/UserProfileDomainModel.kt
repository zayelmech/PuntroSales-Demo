package com.imecatro.demosales.domain.core.profile.model

data class UserProfileDomainModel(
    val storeName: String,
    val storeDescription: String,
    val storeLogoUri: String,
    val whatsapp: String,
    val location: String,
    val language: String,
    val currency: String,
    val isDarkTheme: Boolean,
) {
    companion object {
        val default = UserProfileDomainModel(
            storeName = "Puntro Sales",
            storeDescription = "",
            storeLogoUri = "",
            whatsapp = "",
            location = "",
            language = "en",
            currency = "USD",
            isDarkTheme = false
        )
    }
}
