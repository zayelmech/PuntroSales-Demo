package com.imecatro.products.ui.catalog.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme
import com.imecatro.products.ui.R

/**
 * Screen that shows the progress of preparing the catalog.
 * Transmits confidence and professionalism with a clean checklist.
 */
@Composable
fun PublishCatalogProgressScreen(
    modifier: Modifier = Modifier,
    // For demo/preview purposes, we can pass the current step
    currentStepIndex: Int = 2
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Illustration
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 280.dp, max = 340.dp)
                    .padding(top = 48.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_publish_catalog), // Reusing same illustration for consistency
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(0.75f),
                    contentScale = ContentScale.Fit
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title
                Text(
                    text = stringResource(R.string.publish_catalog_progress_title),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = (-0.5).sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Description
                Text(
                    text = stringResource(R.string.publish_catalog_progress_description),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 24.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Checklist
                ProgressChecklist(currentStepIndex = currentStepIndex)

                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.height(64.dp))

                // Footer
                Text(
                    text = stringResource(R.string.publish_catalog_progress_footer),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.outline,
                        fontWeight = FontWeight.Medium
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

enum class StepState {
    COMPLETED,
    IN_PROGRESS,
    PENDING
}

@Composable
private fun ProgressChecklist(currentStepIndex: Int) {
    val steps = listOf(
        stringResource(R.string.publish_catalog_progress_step_products),
        stringResource(R.string.publish_catalog_progress_step_images),
        stringResource(R.string.publish_catalog_progress_step_processing),
        stringResource(R.string.publish_catalog_progress_step_web),
        stringResource(R.string.publish_catalog_progress_step_ready)
    )

    Column(
        modifier = Modifier.fillMaxWidth(0.85f),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        steps.forEachIndexed { index, title ->
            val state = when {
                index < currentStepIndex -> StepState.COMPLETED
                index == currentStepIndex -> StepState.IN_PROGRESS
                else -> StepState.PENDING
            }
            ChecklistItem(title = title, state = state)
        }
    }
}

@Composable
private fun ChecklistItem(
    title: String,
    state: StepState
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val brandColor = Color(0xFF37C8AB)

        when (state) {
            StepState.COMPLETED -> {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = brandColor,
                    modifier = Modifier.size(28.dp)
                )
            }
            StepState.IN_PROGRESS -> {
                val infiniteTransition = rememberInfiniteTransition(label = "rotation")
                val rotation by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1200, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "rotation"
                )
                Icon(
                    imageVector = Icons.Outlined.Sync,
                    contentDescription = null,
                    tint = brandColor,
                    modifier = Modifier
                        .size(28.dp)
                        .rotate(rotation)
                )
            }
            StepState.PENDING -> {
                Icon(
                    imageVector = Icons.Outlined.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(20.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = if (state == StepState.IN_PROGRESS) FontWeight.Bold else FontWeight.Medium,
                color = when (state) {
                    StepState.COMPLETED -> MaterialTheme.colorScheme.onSurface
                    StepState.IN_PROGRESS -> brandColor
                    StepState.PENDING -> MaterialTheme.colorScheme.outline
                }
            )
        )
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun PublishCatalogProgressPreview() {
    PuntroSalesDemoTheme {
        PublishCatalogProgressScreen(currentStepIndex = 4)
    }
}
