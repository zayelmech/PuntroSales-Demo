package com.imecatro.demosales.profile.ui.model

data class UserProfileUiModel(
    val storeName: String,
    val storeLogoUri: String,
    val language: String,
    val currency: String,
    val isDarkTheme: Boolean
)
