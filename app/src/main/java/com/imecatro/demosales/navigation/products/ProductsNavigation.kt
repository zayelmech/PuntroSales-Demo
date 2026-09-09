package com.imecatro.demosales.navigation.products

import android.content.ClipData
import android.content.Intent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.imecatro.demosales.R
import com.imecatro.demosales.ui.theme.architect.UiStateHandler
import com.imecatro.products.ui.add.views.AddProductComposableStateImpl
import com.imecatro.products.ui.catalog.screens.CatalogAuthenticationScreen
import com.imecatro.products.ui.catalog.screens.PublishCatalogOnboardingScreen
import com.imecatro.products.ui.catalog.screens.PublishCatalogPreviewScreen
import com.imecatro.products.ui.catalog.screens.PublishCatalogProgressScreen
import com.imecatro.products.ui.catalog.screens.PublishCatalogSuccessScreen
import com.imecatro.products.ui.catalog.screens.PublishCatalogSummaryScreen
import com.imecatro.products.ui.catalog.screens.WebCatalogManagementScreen
import com.imecatro.products.ui.catalog.screens.WebCatalogQRScreen
import com.imecatro.products.ui.catalog.viewmodel.AuthenticationViewModel
import com.imecatro.products.ui.catalog.viewmodel.CatalogViewModel
import com.imecatro.products.ui.catalog.viewmodel.WebCatalogManagementViewModel
import com.imecatro.products.ui.categories.screens.CategoriesScreenImpl
import com.imecatro.products.ui.details.viewmodels.ProductsDetailsViewModel
import com.imecatro.products.ui.details.views.DetailsComposableStateImpl
import com.imecatro.products.ui.update.viewmodel.UpdateProductViewModel
import com.imecatro.products.ui.update.views.UpdateProductComposableStateImpl
import kotlinx.coroutines.launch

/**
 * Defines the navigation graph for the Products feature.
 *
 * This includes routes for listing products, adding/editing products, managing categories,
 * and creating catalogs.
 *
 * @param T The type of the root destination for this feature.
 * @param navController The [NavHostController] used for navigation between screens.
 */
@OptIn(ExperimentalMaterial3Api::class)
inline fun <reified T : Any> NavGraphBuilder.productsNavigation(navController: NavHostController) {
    navigation<T>(startDestination = ProductsDestinations.ListAndDetails) {


        composable<ProductsDestinations.ListAndDetails> {
            ListAndDetailsPane(
                onAddProduct = {
                    navController.navigate(ProductsDestinations.Add)
                }, onCreateCatalog = { ids ->
                    navController.navigate(CatalogDestinations.CatalogMaker(ids))
                },
                onManagementAction = {
                    navController.navigate(CatalogDestinations.Management)
                },
                onEditProduct = { id ->
                    navController.navigate(ProductsDestinations.Edit(id))
                }
            )
        }
        composable<ProductsDestinations.Categories> {
            CategoriesScreenImpl(hiltViewModel())
        }
        composable<ProductsDestinations.Details> { backStackEntry ->
            val navArgs = backStackEntry.toRoute<ProductsDestinations.Details>()

            val viewModel: ProductsDetailsViewModel =
                hiltViewModel(creationCallback = { factory: ProductsDetailsViewModel.Factory ->
                    factory.create(
                        navArgs.id
                    )
                })

            DetailsComposableStateImpl(
                viewModel,
                pageSelected = if (navArgs.mode == ProductsDestinations.DetailsOf.Stock) 1 else 0,
                onNavigateBack = {
                    navController.navigate(ProductsDestinations.ListAndDetails) {
                        popUpTo<ProductsDestinations.ListAndDetails> { inclusive = true }
                    }
                },
                onProductDeleted = {
                    navController.navigate(ProductsDestinations.ListAndDetails) {
                        popUpTo<ProductsDestinations.ListAndDetails> { inclusive = true }
                    }
                }, onNavigateToEdit = {
                    navController.navigate(ProductsDestinations.Edit(navArgs.id))
                })

        }
        composable<ProductsDestinations.Add> {

            AddProductComposableStateImpl(hiltViewModel(), onBackToList = {
                navController.navigate(ProductsDestinations.ListAndDetails) {
                    popUpTo<ProductsDestinations.ListAndDetails> { inclusive = true }
                }
            }) {
                navController.navigate(ProductsDestinations.ListAndDetails) {
                    popUpTo<ProductsDestinations.ListAndDetails>()
                }
            }
        }

        composable<ProductsDestinations.Edit> { backStackEntry ->
            val navArgs = backStackEntry.toRoute<ProductsDestinations.Edit>()

            val viewModel: UpdateProductViewModel =
                hiltViewModel(creationCallback = { factory: UpdateProductViewModel.Factory ->
                    factory.create(
                        navArgs.id
                    )
                })


            UpdateProductComposableStateImpl(
                updateProductViewModel = viewModel,
                onEditStock = {
                    val destinationRoute = ProductsDestinations.Details(
                        navArgs.id,
                        ProductsDestinations.DetailsOf.Stock
                    )
                    navController.navigate(destinationRoute) {
                        popUpTo(destinationRoute) { inclusive = true }
                    }
                },
                onBackToList = {
                    navController.navigate(ProductsDestinations.ListAndDetails) {
                        popUpTo<ProductsDestinations.ListAndDetails> { inclusive = true }
                    }
                }) {
                navController.navigate(ProductsDestinations.ListAndDetails) {
                    popUpTo<ProductsDestinations.ListAndDetails>()
                }
            }
        }


        composable<CatalogDestinations.CatalogMaker> { backStackEntry ->
            val navArgs = backStackEntry.toRoute<CatalogDestinations.CatalogMaker>()

            // Scope the ViewModel to this destination entry
            val viewModel: CatalogViewModel =
                hiltViewModel(
                    viewModelStoreOwner = backStackEntry,
                    creationCallback = { f: CatalogViewModel.Factory -> f.create(navArgs.ids) })

            val uiState by viewModel.uiState.collectAsStateWithLifecycle()



            PublishCatalogOnboardingScreen(
                onContinueClick = {
                    navController.navigate(CatalogDestinations.Authentication)
                },
                onCancelClick = {
                    navController.popBackStack()
                },
                onLaterClick = {
                    navController.popBackStack()
                },
            )
        }
        composable<CatalogDestinations.Summary> { backStackEntry ->

            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<CatalogDestinations.CatalogMaker>()
            }
            val viewModel: CatalogViewModel =
                hiltViewModel(parentEntry)

            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            PublishCatalogSummaryScreen(
                summary = uiState,
                onUpdate = viewModel::onUpdateStoreInfo,
                onToggleProduct = viewModel::onToggleProductSelection,
                onContinue = {
                    navController.navigate(CatalogDestinations.Publishing)
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }
        composable<CatalogDestinations.PreviewCatalog> { backStackEntry ->
            val navArgs = backStackEntry.toRoute<CatalogDestinations.PreviewCatalog>()

            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<CatalogDestinations.CatalogMaker>()
            }
            val viewModel: CatalogViewModel =
                hiltViewModel(parentEntry)

            LaunchedEffect(Unit) {
                viewModel.onCatalogUrlConsumed()
            }

            PublishCatalogPreviewScreen(
                previewUrl = navArgs.url,
                onContinueClick = {
                    navController.navigate(CatalogDestinations.Share)
                },
                onCloseClick = {
                    navController.navigate(CatalogDestinations.Management) {
                        popUpTo<CatalogDestinations.CatalogMaker> { inclusive = true }
                    }
                }
            )
        }

        composable<CatalogDestinations.Publishing> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<CatalogDestinations.CatalogMaker>()
            }
            val viewModel: CatalogViewModel =
                hiltViewModel(parentEntry)

            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(uiState.catalogReady) {
                if (uiState.catalogReady) {
                    navController.navigate(CatalogDestinations.PreviewCatalog(uiState.previewUrl)) {
                        // excludes this Publishing from routes
                        popUpTo(CatalogDestinations.Publishing) {
                            inclusive = true
                        }
                    }
                }
            }
            LaunchedEffect(Unit) {
                viewModel.onPublishCatalog()
            }

            PublishCatalogProgressScreen(
                currentStepIndex = uiState.step
            )

            UiStateHandler(
                state = uiState,
                onDismiss = {
                    viewModel.onPublishErrorConsumed()
                    navController.popBackStack()
                },
                showLoading = false
            )
        }

        composable<CatalogDestinations.Share> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<CatalogDestinations.CatalogMaker>()
            }
            val viewModel: CatalogViewModel =
                hiltViewModel(parentEntry)

            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(uiState.catalogUrl) {
                if (uiState.catalogUrl.isNotBlank()) {
                    navController.navigate(
                        CatalogDestinations.Publicated(url = uiState.catalogUrl)
                    ) {
                        popUpTo<CatalogDestinations.CatalogMaker> {
                            inclusive = true
                        }
                    }
                }
            }

            PublishCatalogProgressScreen(
                currentStepIndex = uiState.step
            )
        }


        dialog<CatalogDestinations.Publicated>(
            dialogProperties = DialogProperties(usePlatformDefaultWidth = false)
        ) { backStackEntry ->
            val navArgs = backStackEntry.toRoute<CatalogDestinations.Publicated>()
            val context = LocalContext.current

            PublishCatalogSuccessScreen(
                catalogUrl = navArgs.url,
                onShareWhatsApp = {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, navArgs.url)
                        type = "text/plain"
                        setPackage("com.whatsapp")
                    }
                    try {
                        context.startActivity(sendIntent)
                    } catch (e: Exception) {
                        val fallbackIntent =
                            Intent.createChooser(
                                sendIntent.apply { setPackage(null) },
                                null
                            )
                        context.startActivity(fallbackIntent)
                    }
                },
                onViewCatalog = {
                    val intent = Intent(Intent.ACTION_VIEW, navArgs.url.toUri())
                    context.startActivity(intent)
                },
                onClose = {
                    navController.navigate(CatalogDestinations.Management) {
                        popUpTo<ProductsDestinations.ListAndDetails> { inclusive = false }
                    }
                }
            )
        }
        composable<CatalogDestinations.Authentication> {

            val viewModel: AuthenticationViewModel = hiltViewModel()

            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            val res = LocalResources.current
            val context = LocalContext.current
            val coroutineScope = rememberCoroutineScope()

            CatalogAuthenticationScreen(
                isUserAuthenticated = uiState.isAuthenticated,
                userAlias = uiState.userName,
                onChangeAccount = {
                    viewModel.signOut()
                },
                onBackClick = {
                    navController.popBackStack()
                },
                onGoogleSignInClick = {
                    coroutineScope.launch {
                        try {
                            val credentialManager = CredentialManager.create(context)

                            val googleIdOption = GetGoogleIdOption.Builder()
                                .setFilterByAuthorizedAccounts(false)
                                .setServerClientId(res.getString(R.string.default_web_client_id))
                                .build()

                            val request = GetCredentialRequest.Builder()
                                .addCredentialOption(googleIdOption)
                                .build()

                            val result = credentialManager.getCredential(
                                request = request,
                                context = context
                            )

                            val credential = result.credential

                            if (
                                credential is CustomCredential &&
                                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                            ) {
                                val googleIdTokenCredential =
                                    GoogleIdTokenCredential.createFrom(credential.data)

                                val idToken = googleIdTokenCredential.idToken

                                viewModel.signInWithGoogle(idToken)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                },
                onContinueClick = {
                    navController.navigate(CatalogDestinations.Summary)
                }
            )
        }

        composable<CatalogDestinations.Management> {
            val viewModel: WebCatalogManagementViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val context = LocalContext.current
            val clipboard = LocalClipboard.current
            val scope = rememberCoroutineScope()

            LaunchedEffect(uiState.unpublished) {
                if (uiState.unpublished) {
                    navController.navigate(ProductsDestinations.ListAndDetails) {
                        popUpTo<ProductsDestinations.ListAndDetails> { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }

            WebCatalogManagementScreen(
                state = uiState,
                onBack = { navController.popBackStack() },
                onViewCatalog = {
                    val intent = Intent(Intent.ACTION_VIEW, uiState.catalogUrl.toUri())
                    context.startActivity(intent)
                },
                onShareCatalog = {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, uiState.catalogUrl)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    context.startActivity(shareIntent)
                },
                onUpdateCatalog = {
                    // Navigate to CatalogMaker with all current products or similar logic
                    // For now, let's trigger the refresh in VM
                    //viewModel.onRefreshCatalog()
                    navController.navigate(CatalogDestinations.CatalogMaker(uiState.productIds))
                },
                onCopyLink = {
                    scope.launch {
                        val url = uiState.catalogUrl.toUri()
                        val clipEntry =
                            ClipEntry(ClipData.newUri(context.contentResolver, "Copied", url))
                        clipboard.setClipEntry(clipEntry)
                    }
                },
                onViewQr = {
                    navController.navigate(CatalogDestinations.QRView(uiState.catalogUrl))
                },
                onUnpublishCatalog = {
                    viewModel.onUnpublishCatalog()
                },
                onHelp = {
                    // Handle help
                }
            )

            UiStateHandler(state = uiState, onDismiss = viewModel::onErrorConsumed)
        }

        composable<CatalogDestinations.QRView> { backStackEntry ->
            val navArgs = backStackEntry.toRoute<CatalogDestinations.QRView>()
            val context = LocalContext.current

            WebCatalogQRScreen(
                url = navArgs.url,
                onBack = { navController.popBackStack() },
                onShareQr = { bitmap: android.graphics.Bitmap ->
                    // Logic to share the bitmap
                    val path = android.provider.MediaStore.Images.Media.insertImage(
                        context.contentResolver,
                        bitmap,
                        "CatalogQR",
                        null
                    )
                    if (path != null) {
                        val uri = path.toUri()
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "image/jpeg"
                            putExtra(Intent.EXTRA_STREAM, uri)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Compartir QR"))
                    }
                }
            )
        }
    }
}
