package com.imecatro.demosales.profile.ui.mappers

import com.imecatro.demosales.profile.domain.model.UserProfileDomainModel
import com.imecatro.demosales.profile.ui.model.UserProfileUiModel

fun UserProfileDomainModel.toUiModel() = UserProfileUiModel(
    storeName = storeName,
    storeLogoUri = storeLogoUri,
    language = language,
    currency = currency,
    isDarkTheme = isDarkTheme
)

fun UserProfileUiModel.toDomainModel() = UserProfileDomainModel(
    storeName = storeName,
    storeLogoUri = storeLogoUri,
    language = language,
    currency = currency,
    isDarkTheme = isDarkTheme
)
