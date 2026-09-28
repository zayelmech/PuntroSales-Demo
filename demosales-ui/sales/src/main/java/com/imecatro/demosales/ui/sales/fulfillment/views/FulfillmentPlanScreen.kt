package com.imecatro.demosales.ui.sales.fulfillment.views

import android.content.ActivityNotFoundException
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.imecatro.demosales.ui.sales.R
import com.imecatro.demosales.ui.sales.fulfillment.model.FulfillmentOrderUiModel
import com.imecatro.demosales.ui.sales.fulfillment.model.FulfillmentPlanUiState
import com.imecatro.demosales.ui.sales.fulfillment.model.FulfillmentProductUiModel
import com.imecatro.demosales.ui.sales.fulfillment.model.formatQuantity
import com.imecatro.demosales.ui.sales.fulfillment.model.shoppingListText
import com.imecatro.demosales.ui.sales.fulfillment.viewmodel.FulfillmentPlanViewModel
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme
import java.math.BigDecimal

@Composable
fun FulfillmentPlanScreenImpl(
    viewModel: FulfillmentPlanViewModel,
    onBack: () -> Unit,
    onOpenOrder: (Long) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val locale = LocalLocale.current.platformLocale
    val shoppingListTitle = stringResource(R.string.fulfillment_shopping_list)
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.refresh() }

    FulfillmentPlanScreen(
        state = state,
        onBack = onBack,
        onOpenOrder = onOpenOrder,
        onRetry = viewModel::refresh,
        onShare = {
            val text = state.shoppingListText(shoppingListTitle, locale)
            if (text.isNotBlank()) {
                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, shoppingListTitle)
                    putExtra(Intent.EXTRA_TEXT, text)
                }
                try {
                    context.startActivity(Intent.createChooser(sendIntent, shoppingListTitle))
                } catch (_: ActivityNotFoundException) {
                    Toast.makeText(context, R.string.fulfillment_share_error, Toast.LENGTH_LONG).show()
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FulfillmentPlanScreen(
    state: FulfillmentPlanUiState,
    onBack: () -> Unit,
    onOpenOrder: (Long) -> Unit,
    onRetry: () -> Unit,
    onShare: () -> Unit
) {
    var onlyMissing by rememberSaveable { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.fulfillment_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.fulfillment_back))
                    }
                },
                actions = {
                    IconButton(onClick = onRetry, enabled = !state.isLoading) {
                        Icon(Icons.Default.Refresh, stringResource(R.string.fulfillment_refresh))
                    }
                }
            )
        },
        bottomBar = {
            if (!state.isLoading && !state.hasError && state.products.isNotEmpty()) {
                Surface(tonalElevation = 3.dp) {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Button(
                            onClick = onShare,
                            enabled = state.canShare,
                            modifier = Modifier.widthIn(max = 840.dp).fillMaxWidth().padding(16.dp)
                        ) {
                            Icon(Icons.Default.Share, null)
                            Spacer(Modifier.size(8.dp))
                            Text(stringResource(R.string.fulfillment_share))
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.TopCenter) {
            when {
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                state.hasError -> PlanMessage(
                    title = stringResource(R.string.fulfillment_error),
                    description = stringResource(R.string.fulfillment_error_description),
                    modifier = Modifier.align(Alignment.Center),
                    action = {
                        Button(onClick = onRetry) { Text(stringResource(R.string.fulfillment_retry)) }
                    }
                )
                state.products.isEmpty() -> PlanMessage(
                    title = stringResource(R.string.fulfillment_empty),
                    description = stringResource(R.string.fulfillment_empty_description),
                    modifier = Modifier.align(Alignment.Center)
                )
                else -> LazyColumn(
                    modifier = Modifier.widthIn(max = 840.dp).fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item(key = "summary") { PlanSummary(state) }
                    if (state.needsReview) {
                        item(key = "review") {
                            Text(
                                stringResource(R.string.fulfillment_review_notice),
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    item(key = "filters") {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = !onlyMissing,
                                onClick = { onlyMissing = false },
                                label = { Text(stringResource(R.string.fulfillment_all, state.products.size)) }
                            )
                            FilterChip(
                                selected = onlyMissing,
                                onClick = { onlyMissing = true },
                                label = { Text(stringResource(R.string.fulfillment_to_buy, state.shoppingList.size)) }
                            )
                        }
                    }
                    if (onlyMissing && state.shoppingList.isEmpty()) {
                        item(key = "covered") {
                            PlanMessage(
                                title = stringResource(
                                    if (state.needsReview) R.string.fulfillment_review_title
                                    else R.string.fulfillment_covered
                                ),
                                description = stringResource(
                                    if (state.needsReview) R.string.fulfillment_review_notice
                                    else R.string.fulfillment_covered_description
                                )
                            )
                        }
                    }
                    items(
                        if (onlyMissing) state.shoppingList else state.products,
                        key = { it.id }
                    ) { product ->
                        FulfillmentProductCard(product, onOpenOrder)
                    }
                }
            }
        }
    }
}

@Composable
private fun PlanSummary(state: FulfillmentPlanUiState) {
    val locale = LocalLocale.current.platformLocale
    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(stringResource(R.string.fulfillment_scope), style = MaterialTheme.typography.titleMedium)
            Text(
                stringResource(R.string.fulfillment_order_summary, state.orderCount, state.clientCount),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(stringResource(R.string.fulfillment_demand_total), style = MaterialTheme.typography.labelLarge)
            // Keep units separate: kilograms and pieces must never be added into one total.
            val unknownUnit = stringResource(R.string.fulfillment_unknown_unit)
            val totals = state.products.groupBy { it.unit ?: unknownUnit }.map { (unit, products) ->
                val quantity = products.fold(BigDecimal.ZERO) { total, product -> total + product.demand }
                "${quantity.formatQuantity(locale)} $unit"
            }
            Text(totals.joinToString(" · "), style = MaterialTheme.typography.headlineSmall)
            if (!state.needsReview && state.shoppingList.isEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, null)
                    Text(stringResource(R.string.fulfillment_covered), style = MaterialTheme.typography.titleSmall)
                }
            } else {
                Text(
                    pluralStringResource(R.plurals.fulfillment_purchase_summary, state.shoppingList.size, state.shoppingList.size),
                    style = MaterialTheme.typography.titleSmall
                )
            }
            Text(stringResource(R.string.fulfillment_stock_explanation), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun FulfillmentProductCard(product: FulfillmentProductUiModel, onOpenOrder: (Long) -> Unit) {
    val locale = LocalLocale.current.platformLocale
    var expanded by rememberSaveable(product.id) { mutableStateOf(false) }
    val unit = product.unit ?: stringResource(R.string.fulfillment_unknown_unit)
    val unknown = stringResource(R.string.fulfillment_unknown)
    val hasShortage = product.missing?.signum() == 1
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(product.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(
                stringResource(R.string.fulfillment_product_reference, product.id, unit),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                QuantityColumn(
                    stringResource(R.string.fulfillment_demand),
                    product.demand.formatQuantity(locale), unit, Modifier.weight(1f)
                )
                QuantityColumn(
                    stringResource(R.string.fulfillment_available),
                    product.available?.formatQuantity(locale) ?: unknown, unit, Modifier.weight(1f)
                )
                QuantityColumn(
                    stringResource(R.string.fulfillment_missing),
                    product.missing?.formatQuantity(locale) ?: unknown, unit, Modifier.weight(1f),
                    if (hasShortage) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }
            HorizontalDivider()
            TextButton(onClick = { expanded = !expanded }, contentPadding = PaddingValues(0.dp)) {
                Text(
                    stringResource(
                        if (expanded) R.string.fulfillment_hide_orders else R.string.fulfillment_related_orders,
                        product.orders.size
                    ),
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Default.ArrowDropDown, null)
            }
            if (expanded) {
                product.orders.forEach { order ->
                    Surface(
                        onClick = { onOpenOrder(order.id) },
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surfaceContainer
                    ) {
                        Row(
                            Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(stringResource(R.string.fulfillment_order, order.id), style = MaterialTheme.typography.labelLarge)
                                Text(
                                    order.clientName.ifBlank { stringResource(R.string.fulfillment_no_client) },
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Text("${order.quantity.formatQuantity(locale)} $unit", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuantityColumn(
    label: String,
    quantity: String,
    unit: String,
    modifier: Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(quantity, style = MaterialTheme.typography.titleLarge, color = color)
        Text(unit, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun PlanMessage(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    action: @Composable () -> Unit = {}
) {
    Column(
        modifier.widthIn(max = 480.dp).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        Text(description, style = MaterialTheme.typography.bodyMedium)
        action()
    }
}

@Preview(showBackground = true, locale = "es")
@Preview(showBackground = true, locale = "es", fontScale = 1.5f)
@Composable
private fun FulfillmentPlanPreview() {
    PuntroSalesDemoTheme {
        FulfillmentPlanScreen(
            state = FulfillmentPlanUiState(
                isLoading = false, orderCount = 3, clientCount = 2,
                products = listOf(
                    FulfillmentProductUiModel(
                        1, "Café de especialidad", "kg", BigDecimal("8.5"), BigDecimal("5"), BigDecimal("3.5"),
                        listOf(FulfillmentOrderUiModel(102, "María López", BigDecimal("8.5")))
                    ),
                    FulfillmentProductUiModel(
                        2, "Miel de abeja", "pz", BigDecimal("12"), BigDecimal("20"), BigDecimal.ZERO,
                        listOf(FulfillmentOrderUiModel(103, "Luis Pérez", BigDecimal("12")))
                    )
                )
            ), onBack = {}, onOpenOrder = {}, onRetry = {}, onShare = {}
        )
    }
}
