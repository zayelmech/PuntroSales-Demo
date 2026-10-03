package com.imecatro.demosales.onboarding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.imecatro.demosales.R
import com.imecatro.demosales.navigation.products.CatalogDestinations
import com.imecatro.demosales.navigation.products.ProductsDestinations
import com.imecatro.demosales.navigation.sales.SalesDestinations
import com.imecatro.demosales.ui.NavigationDirections
import com.imecatro.demosales.ui.navigateToRoot
import kotlinx.coroutines.launch

@Composable
fun OnboardingAppContent(
    navController: NavHostController,
    component: OnboardingComponent,
    viewModel: OnboardingViewModel = hiltViewModel(),
    content: @Composable (banner: @Composable () -> Unit, onPlanLoaded: (Boolean, Boolean, Boolean) -> Unit) -> Unit
) {
    val progress by viewModel.progress.collectAsStateWithLifecycle()
    val showIntroduction by viewModel.showIntroduction.collectAsStateWithLifecycle()
    val productIds by viewModel.productIds.collectAsStateWithLifecycle()
    val catalogPublished by viewModel.catalogPublished.collectAsStateWithLifecycle()
    var showChecklist by rememberSaveable { mutableStateOf(false) }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val orderHint = stringResource(R.string.onboarding_order_hint)
    val completedMessage = stringResource(R.string.onboarding_complete)

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.refreshCatalog() }

    val openStep: (OnboardingStep) -> Unit = { step ->
        showChecklist = false
        navController.openOnboardingStep(step, productIds, catalogPublished)
        if (step == OnboardingStep.ORDER && OnboardingStep.ORDER !in progress.steps) {
            scope.launch { snackbar.showSnackbar(orderHint) }
        }
    }

    Box(Modifier.fillMaxSize()) {
        content({ component.Banner(progress, openStep, { showChecklist = true }) }, viewModel::planLoaded)
        SnackbarHost(snackbar, Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(bottom = 80.dp))
    }

    if (showIntroduction) {
        OnboardingIntroduction(
            onStart = {
                viewModel.dismissIntroduction()
                showChecklist = true
            },
            onLater = viewModel::dismissIntroduction
        )
    }
    if (showChecklist && !showIntroduction) {
        OnboardingChecklist(progress, openStep, { showChecklist = false })
    }

    LaunchedEffect(progress.completed, progress.completionAcknowledged, showIntroduction) {
        if (progress.completed && !progress.completionAcknowledged && !showIntroduction) {
            showChecklist = false
            snackbar.showSnackbar(completedMessage)
            viewModel.acknowledgeCompletion()
        }
    }
}

internal fun NavHostController.openOnboardingStep(step: OnboardingStep, productIds: List<Long>, catalogPublished: Boolean) {
    when (step) {
        OnboardingStep.BUSINESS -> navigateToRoot(NavigationDirections.ProfileFeature)
        OnboardingStep.PRODUCTS -> {
            navigateToRoot(NavigationDirections.ProductsFeature)
            navigate(ProductsDestinations.Add) { launchSingleTop = true }
        }
        OnboardingStep.ORDER -> {
            navigateToRoot(NavigationDirections.SalesFeature)
            navigate(SalesDestinations.Add()) { launchSingleTop = true }
        }
        OnboardingStep.FULFILLMENT -> {
            navigateToRoot(NavigationDirections.SalesFeature)
            navigate(SalesDestinations.FulfillmentPlan) { launchSingleTop = true }
        }
        OnboardingStep.CATALOG -> {
            navigateToRoot(NavigationDirections.ProductsFeature)
            if (catalogPublished) navigate(CatalogDestinations.Management) { launchSingleTop = true }
            else navigate(CatalogDestinations.CatalogMaker(productIds)) { launchSingleTop = true }
        }
    }
}
