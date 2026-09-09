package com.imecatro.products.ui.catalog.screens

import android.content.ClipData
import android.content.res.Configuration
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme
import com.imecatro.products.ui.R
import com.imecatro.products.ui.catalog.components.MetricItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val BrandColor @Composable get() = MaterialTheme.colorScheme.primary

/**
 * Modern success screen after publishing the catalog.
 * Designed to generate a sense of achievement and encourage immediate sharing.
 * Fully compatible with Material 3 Dark Mode.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublishCatalogSuccessScreen(
    catalogUrl: String,
    onShareWhatsApp: () -> Unit,
    onViewCatalog: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboard = LocalClipboard.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.publish_catalog_success_title),
                        fontWeight = FontWeight.Bold
                    )
                }, actions = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                }
            )
        }
    ) { paddingValues ->
        PublishCatalogSuccessContent(
            catalogUrl = catalogUrl,
            onShareWhatsApp = onShareWhatsApp,
            onViewCatalog = onViewCatalog,
            onCopyLink = {
                scope.launch {
                    val url = catalogUrl.toUri()
                    val clipEntry =
                        ClipEntry(ClipData.newUri(context.contentResolver, "Copied", url))
                    clipboard.setClipEntry(clipEntry)
                }
            },
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun PublishCatalogSuccessContent(
    catalogUrl: String,
    onShareWhatsApp: () -> Unit,
    onViewCatalog: () -> Unit,
    onCopyLink: () -> Unit,
    modifier: Modifier = Modifier,
    showFullInfo: Boolean = true
) {
    var showConfetti by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        showConfetti = true
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (showFullInfo) {
            Spacer(modifier = Modifier.height(25.dp))

            // Celebration Illustration & Success Check
            SuccessHeader(showConfetti)

            Spacer(modifier = Modifier.height(12.dp))

            // Description
            Text(
                text = stringResource(R.string.publish_catalog_success_description),
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 24.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Premium Catalog URL Card
            UrlCard(
                url = catalogUrl,
                onCopy = onCopyLink
            )

            Spacer(modifier = Modifier.height(24.dp))
        } else {
            Spacer(modifier = Modifier.height(24.dp))
            // Only show URL text in compact mode
            Text(
                text = catalogUrl,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )
        }

        // Main Actions - Always show
        PrimaryActions(
            onShareWhatsApp = onShareWhatsApp,
            onViewCatalog = onViewCatalog,
            onCopyLink = onCopyLink
        )

//        if (showFullInfo) {
//            Spacer(modifier = Modifier.height(48.dp))
//            // ...
//
//            // Metrics Section
//            MetricsSection(
//                totalProducts = totalProducts,
//                totalImages = totalImages
//            )
//
//            Spacer(modifier = Modifier.height(48.dp))
//
//            // Next Steps Section
//            NextStepsSection()
//
//            Spacer(modifier = Modifier.height(64.dp))
//        } else {
//            Spacer(modifier = Modifier.height(32.dp))
//        }
    }
}

@Composable
private fun SuccessHeader(animate: Boolean) {
    val scale by animateFloatAsState(
        targetValue = if (animate) 1f else 0.8f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessLow),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .size(160.dp)
            .scale(scale),
        contentAlignment = Alignment.Center
    ) {
        // Decorative background circles (minimalist confetti)
        if (animate) {
            repeat(6) { i ->
                val angle = i * 60f
                ConfettiPiece(angle)
            }
        }

        // Central Success Check Circle
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(BrandColor.copy(alpha = 0.1f), CircleShape)
                .border(2.dp, BrandColor.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = BrandColor,
                modifier = Modifier.size(56.dp)
            )
        }
    }
}

@Composable
private fun ConfettiPiece(angle: Float) {
    val transition = rememberInfiniteTransition(label = "confetti")
    val translation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "translation"
    )

    Box(
        modifier = Modifier
            .size(8.dp)
            .offset(y = (-60 - translation).dp)
            .rotate(angle)
            .background(BrandColor.copy(alpha = 0.6f), CircleShape)
    )
}

// Helper to rotate the offset
// Usamos el de compose.ui.draw

@Composable
private fun UrlCard(url: String, onCopy: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.publish_catalog_success_url_title),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant,
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { onCopy() }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Text(
                    text = url,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = null,
                    tint = BrandColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun PrimaryActions(
    onShareWhatsApp: () -> Unit,
    onViewCatalog: () -> Unit,
    onCopyLink: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onShareWhatsApp,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandColor
            ),
            shape = RoundedCornerShape(20.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Icon(Icons.Default.Share, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.publish_catalog_success_btn_share_whatsapp),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }

        OutlinedButton(
            onClick = onViewCatalog,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(20.dp),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Icon(
                Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.publish_catalog_success_btn_view),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        }

        TextButton(
            onClick = onCopyLink,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.publish_catalog_success_btn_copy),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = BrandColor
                )
            )
        }
    }
}


@Composable
private fun MetricsSection(totalProducts: Int, totalImages: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        MetricItem(
            icon = Icons.Outlined.Inventory2,
            value = totalProducts.toString(),
            label = stringResource(R.string.publish_catalog_success_metrics_products),
            modifier = Modifier.weight(1f)
        )
        MetricItem(
            icon = Icons.Outlined.Image,
            value = totalImages.toString(),
            label = stringResource(R.string.publish_catalog_success_metrics_images),
            modifier = Modifier.weight(1f)
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Outlined.Update,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = stringResource(R.string.publish_catalog_success_metrics_last_update),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(R.string.publish_catalog_success_metrics_now),
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun NextStepsSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(R.string.publish_catalog_success_next_steps_title),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
        Spacer(modifier = Modifier.height(20.dp))

        StepItem(stringResource(R.string.publish_catalog_success_step_share))
        StepItem(stringResource(R.string.publish_catalog_success_step_update))
        StepItem(stringResource(R.string.publish_catalog_success_step_keep_updated))
    }
}

@Composable
private fun StepItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.CheckCircle,
            contentDescription = null,
            tint = BrandColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
private fun PublishCatalogSuccessPreview() {
    PuntroSalesDemoTheme {
        PublishCatalogSuccessScreen(
            catalogUrl = "https://puntosales.app/mi-tienda-increible",
            onShareWhatsApp = {},
            onViewCatalog = {}, {}
        )
    }
}

@Preview(
    showBackground = true,
    device = "spec:width=411dp,height=891dp",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun PublishCatalogSuccessDarkPreview() {
    PuntroSalesDemoTheme {
        PublishCatalogSuccessScreen(
            catalogUrl = "https://puntosales.app/mi-tienda-increible",
            onShareWhatsApp = {},
            onViewCatalog = {}, {}
        )
    }
}
