package com.imecatro.demosales.profile.ui.mappers

import com.imecatro.demosales.domain.core.model.Languages
import com.imecatro.demosales.profile.domain.model.UserProfileDomainModel
import com.imecatro.demosales.profile.ui.model.UserProfileUiModel

fun UserProfileDomainModel.toUiModel() = UserProfileUiModel(
    storeName = storeName,
    storeLogoUri = storeLogoUri,
    language = Languages.fromTag(language).displayName,
    currency = currency,
    isDarkTheme = isDarkTheme
)

fun UserProfileUiModel.toDomainModel() = UserProfileDomainModel(
    storeName = storeName,
    storeLogoUri = storeLogoUri,
    language = Languages.fromDisplayName(language).tag,
    currency = currency,
    isDarkTheme = isDarkTheme
)
