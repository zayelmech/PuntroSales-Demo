package com.imecatro.demosales.navigation.profile

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.imecatro.demosales.profile.ui.viewmodels.ProfileViewModel
import com.imecatro.demosales.profile.ui.views.ProfileSettingsStateImpl

/**
 * Route for [ProfileSettingsStateImpl]
 */
inline fun <reified T : Any> NavGraphBuilder.profileNavigation(navController: NavHostController) {
    navigation<T>(startDestination = ProfileRoute) {
        composable<ProfileRoute> {
            ProfileSettingsStateImpl(
                hiltViewModel<ProfileViewModel>(),
                onBack = { navController.popBackStack() }
            )
        }
    }
}