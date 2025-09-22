package com.imecatro.demosales.navigation.sales

import android.os.Parcelable
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableSupportingPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import com.imecatro.demosales.ui.sales.details.viewmodel.TicketDetailsViewModel
import com.imecatro.demosales.ui.sales.details.views.TicketDetailsComposableImpl
import com.imecatro.demosales.ui.sales.list.views.SalesListComposableStateImpl
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ListAndDetailsSalesPane(
    onAddSale: () -> Unit = {},
    onEditSale: (Long) -> Unit = {},
    onDuplicateSale: (Long) -> Unit = {}
) {

    val navigator = rememberSupportingPaneScaffoldNavigator<MySale>()
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

@Parcelize
class MySale(val id: Long) : Parcelable