package com.imecatro.demosales.navigation.sales

import android.content.res.Configuration
import android.os.Parcelable
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.NavigableSupportingPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalConfiguration
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.imecatro.demosales.ui.sales.details.viewmodel.TicketDetailsViewModel
import com.imecatro.demosales.ui.sales.details.views.TicketDetailsComposableImpl
import com.imecatro.demosales.ui.sales.list.views.SalesListComposableStateImpl
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

/**
 * A Composable that provides an adaptive list-detail layout for sales.
 *
 * It uses [NavigableSupportingPaneScaffold] to display the sales list in the main pane
 * and sale details in a supporting pane when a sale is selected.
 *
 * @param onAddSale Callback invoked when the user chooses to add a new sale.
 * @param onEditSale Callback invoked when the user chooses to edit an existing sale.
 * @param onDuplicateSale Callback invoked when the user chooses to duplicate a sale.
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ListAndDetailsSalesPane(
    onAddSale: () -> Unit = {},
    onEditSale: (Long) -> Unit = {},
    onDuplicateSale: (Long) -> Unit = {}
) {
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val isPortrait = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
    val directive = calculatePaneScaffoldDirective(adaptiveInfo)
    val customDirective = if (isPortrait) {
        directive.copy(maxVerticalPartitions = 1)
    } else {
        directive
    }

    val navigator = rememberSupportingPaneScaffoldNavigator<MySale>(
        scaffoldDirective = customDirective
    )
    val scope = rememberCoroutineScope()

    val viewModel: TicketDetailsViewModel =
        hiltViewModel(creationCallback = { f: TicketDetailsViewModel.Factory -> f.create(0L) })

    NavigableSupportingPaneScaffold(
        navigator = navigator,
        mainPane = {
            AnimatedPane {
                SalesListComposableStateImpl(salesListViewModel = hiltViewModel()) { id ->
                    if (id != null) {
                        scope.launch {
                            viewModel.loadSaleDetailsBy(id)
                            navigator.navigateTo(
                                pane = SupportingPaneScaffoldRole.Supporting,
                                MySale(id)
                            )
                        }
                    } else {
                        onAddSale()
                    }
                }
            }
        }, supportingPane = {

            AnimatedPane {
                navigator.currentDestination?.contentKey?.let { navArgs ->

                    TicketDetailsComposableImpl(
                        ticketDetailsVM = viewModel, saleId = navArgs.id,
                        onEditTicket = onEditSale,
                        onDuplicateTicket = onDuplicateSale,
                        onBackToList = {
                            scope.launch { navigator.navigateBack() }
                        }
                    )
                }
            }
        }
    )
}

/**
 * Parcelable class used for navigation within the [ListAndDetailsSalesPane].
 *
 * @property id The ID of the sale.
 */
@Parcelize
class MySale(val id: Long) : Parcelable