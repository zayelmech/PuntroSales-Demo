package com.imecatro.demosales.profile.ui.model

data class UserProfileUiModel(
    val storeName: String,
    val storeDescription: String,
    val storeLogoUri: String,
    val whatsapp: String,
    val location: String,
    val language: String,
    val currency: String,
    val isDarkTheme: Boolean
)
