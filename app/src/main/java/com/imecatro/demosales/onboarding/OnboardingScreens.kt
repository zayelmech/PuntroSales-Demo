package com.imecatro.demosales.onboarding

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material.icons.outlined.Web
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.imecatro.demosales.R
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme

internal val OnboardingStep.title: Int get() = when (this) {
    OnboardingStep.BUSINESS -> R.string.onboarding_business
    OnboardingStep.PRODUCTS -> R.string.onboarding_products
    OnboardingStep.ORDER -> R.string.onboarding_order
    OnboardingStep.FULFILLMENT -> R.string.onboarding_fulfillment
    OnboardingStep.CATALOG -> R.string.onboarding_catalog
}

internal val OnboardingStep.description: Int get() = when (this) {
    OnboardingStep.BUSINESS -> R.string.onboarding_business_hint
    OnboardingStep.PRODUCTS -> R.string.onboarding_products_hint
    OnboardingStep.ORDER -> R.string.onboarding_order_hint
    OnboardingStep.FULFILLMENT -> R.string.onboarding_fulfillment_hint
    OnboardingStep.CATALOG -> R.string.onboarding_catalog_hint
}

private val OnboardingStep.icon: ImageVector get() = when (this) {
    OnboardingStep.BUSINESS -> Icons.Outlined.Store
    OnboardingStep.PRODUCTS -> Icons.Outlined.Inventory2
    OnboardingStep.ORDER -> Icons.AutoMirrored.Outlined.ReceiptLong
    OnboardingStep.FULFILLMENT -> Icons.Outlined.ShoppingCart
    OnboardingStep.CATALOG -> Icons.Outlined.Web
}

@Composable
fun OnboardingIntroduction(onStart: () -> Unit, onLater: () -> Unit) {
    Dialog(onDismissRequest = onLater, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        OnboardingIntroductionContent(onStart = onStart, onLater = onLater)
    }
}

@Composable
internal fun OnboardingIntroductionContent(
    onStart: () -> Unit,
    onLater: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    0f to colors.primaryContainer.copy(alpha = 0.58f),
                    0.42f to colors.background,
                    1f to colors.surfaceContainerLow
                )
            )
            .testTag("onboarding-introduction")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.widthIn(max = 560.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = colors.primary,
                    contentColor = colors.onPrimary,
                    shadowElevation = 8.dp,
                    modifier = Modifier.size(76.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.Store,
                            contentDescription = null,
                            modifier = Modifier.size(38.dp)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))
                Surface(
                    shape = CircleShape,
                    color = colors.secondaryContainer,
                    contentColor = colors.onSecondaryContainer
                ) {
                    Text(
                        text = stringResource(R.string.onboarding_intro_badge),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Spacer(Modifier.height(14.dp))
                Text(
                    text = stringResource(R.string.onboarding_welcome_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = colors.onBackground,
                    modifier = Modifier.semantics { heading() }
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = stringResource(R.string.onboarding_intro),
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp, lineHeight = 25.sp),
                    color = colors.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(18.dp))

                OnboardingRouteCard()

                Spacer(Modifier.height(14.dp))
                Text(
                    text = stringResource(R.string.onboarding_flexible),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(14.dp))
                Button(
                    onClick = onStart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("onboarding-start"),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text(stringResource(R.string.onboarding_start))
                    Spacer(Modifier.width(10.dp))
                    Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null)
                }
                TextButton(
                    onClick = onLater,
                    modifier = Modifier.fillMaxWidth().testTag("onboarding-later")
                ) {
                    Text(stringResource(R.string.onboarding_later))
                }
            }
        }
    }
}

@Composable
private fun OnboardingRouteCard() {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            Text(
                text = stringResource(R.string.onboarding_intro_plan_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(10.dp))
            OnboardingStep.entries.forEachIndexed { index, step ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(step.icon, contentDescription = null, modifier = Modifier.size(19.dp))
                            }
                        }
                        if (index != OnboardingStep.entries.lastIndex) {
                            Box(
                                Modifier
                                    .width(2.dp)
                                    .height(8.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.outlineVariant)
                            )
                        }
                    }
                    Text(
                        text = stringResource(step.title),
                        modifier = Modifier.padding(
                            start = 14.dp,
                            bottom = if (index == OnboardingStep.entries.lastIndex) 0.dp else 8.dp
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingBanner(
    progress: OnboardingProgress,
    onOpenStep: (OnboardingStep) -> Unit,
    onChecklist: () -> Unit
) {
    val next = progress.nextStep ?: return
    if (progress.completed) return
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("onboarding-banner"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "${progress.steps.size}/5",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Column(Modifier.padding(start = 12.dp)) {
                    Text(
                        stringResource(R.string.onboarding_title),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.semantics { heading() }
                    )
                    Text(
                        if (next == OnboardingStep.PRODUCTS) {
                            stringResource(R.string.onboarding_product_progress, progress.productCount)
                        } else {
                            stringResource(next.title)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.82f)
                    )
                }
            }
            LinearProgressIndicator(
                progress = { progress.steps.size / OnboardingStep.entries.size.toFloat() },
                modifier = Modifier.fillMaxWidth().height(5.dp).clip(CircleShape),
                trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f)
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { onOpenStep(next) },
                    modifier = Modifier.testTag("onboarding-continue")
                ) {
                    Text(stringResource(R.string.onboarding_continue))
                    Spacer(Modifier.width(6.dp))
                    Icon(
                        Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
                TextButton(onClick = onChecklist, modifier = Modifier.testTag("onboarding-checklist")) {
                    Text(stringResource(R.string.onboarding_all_steps))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingChecklist(
    progress: OnboardingProgress,
    onOpenStep: (OnboardingStep) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        OnboardingChecklistContent(progress, onOpenStep, onDismiss)
    }
}

@Composable
internal fun OnboardingChecklistContent(
    progress: OnboardingProgress,
    onOpenStep: (OnboardingStep) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier.fillMaxWidth().testTag("onboarding-steps")) {
        item {
            Column(Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                Text(
                    stringResource(R.string.onboarding_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.semantics { heading() }
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    stringResource(R.string.onboarding_progress, progress.steps.size),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { progress.steps.size / OnboardingStep.entries.size.toFloat() },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape)
                )
            }
        }
        items(OnboardingStep.entries, key = { it.key }) { step ->
            val done = step in progress.steps
            Surface(
                onClick = { onOpenStep(step) },
                modifier = Modifier.fillMaxWidth().testTag("onboarding-step-${step.key}")
            ) {
                ListItem(
                    headlineContent = { Text(stringResource(step.title), fontWeight = FontWeight.Medium) },
                    supportingContent = {
                        Column {
                            Text(stringResource(step.description))
                            if (step == OnboardingStep.PRODUCTS && !done) {
                                Text(
                                    stringResource(R.string.onboarding_product_progress, progress.productCount),
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    },
                    leadingContent = {
                        Icon(
                            if (done) Icons.Default.CheckCircle else step.icon,
                            stringResource(if (done) R.string.onboarding_done else R.string.onboarding_pending),
                            tint = if (done) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                )
            }
        }
        item {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            ) {
                Text(stringResource(R.string.onboarding_close))
            }
        }
    }
}

@Preview(
    name = "Welcome · Spanish",
    showBackground = true,
    showSystemUi = true,
    device = "spec:parent=pixel_5,navigation=buttons",
    locale = "es"
)
@Composable
fun OnboardingIntroductionPreview() {
    PuntroSalesDemoTheme {
        OnboardingIntroductionContent(onStart = {}, onLater = {})
    }
}

@Preview(
    name = "Welcome · Dark",
    showBackground = true,
    widthDp = 420,
    heightDp = 900,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun OnboardingIntroductionDarkPreview() {
    PuntroSalesDemoTheme(darkTheme = true) {
        OnboardingIntroductionContent(onStart = {}, onLater = {})
    }
}

@Preview(name = "Banner · In progress", showBackground = true, widthDp = 420)
@Composable
fun OnboardingBannerPreview() {
    PuntroSalesDemoTheme {
        OnboardingBanner(
            progress = OnboardingProgress(
                steps = setOf(OnboardingStep.BUSINESS, OnboardingStep.PRODUCTS),
                productCount = 5
            ),
            onOpenStep = {},
            onChecklist = {}
        )
    }
}

@Preview(name = "Checklist · Mixed progress", showBackground = true, widthDp = 420, heightDp = 900)
@Composable
fun OnboardingChecklistPreview() {
    PuntroSalesDemoTheme {
        Surface {
            OnboardingChecklistContent(
                progress = OnboardingProgress(
                    steps = setOf(OnboardingStep.BUSINESS, OnboardingStep.ORDER),
                    productCount = 3
                ),
                onOpenStep = {},
                onDismiss = {}
            )
        }
    }
}
