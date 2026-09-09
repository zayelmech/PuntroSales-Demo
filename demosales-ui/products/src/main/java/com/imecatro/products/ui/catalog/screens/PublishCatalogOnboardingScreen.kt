package com.imecatro.products.ui.catalog.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material.icons.outlined.WebAssetOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
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
 * Screen that invites the user to publish their catalog online for the first time.
 * Designed with Material 3, focusing on a clean, professional, and friendly UI.
 * Improved for responsiveness and tablet support, ensuring action buttons are always accessible.
 */
@Composable
fun PublishCatalogOnboardingScreen(
    onContinueClick: () -> Unit,
    onLaterClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val windowWidth = with(LocalDensity.current) {
        LocalWindowInfo.current.containerSize.width.toDp()
    }
    val isTablet = windowWidth >= 600.dp

    if (isTablet) {
        PublishCatalogOnboardingTablet(onContinueClick, onLaterClick, onCancelClick, modifier)
    } else {
        PublishCatalogOnboardingMobile(
            onContinueClick,
            onLaterClick = onLaterClick,
            onCancelClick = onCancelClick,
            modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PublishCatalogOnboardingMobile(
    onContinueClick: () -> Unit,
    onLaterClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.publish_catalog_title),
                        fontWeight = FontWeight.Bold
                    )
                }, navigationIcon = {
                    IconButton(onClick = onCancelClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                })
        },
        bottomBar = {
            Surface(
                tonalElevation = 2.dp,
                shadowElevation = 8.dp
            ) {
                OnboardingButtons(
                    onContinueClick = onContinueClick,
                    onLaterClick = onLaterClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .navigationBarsPadding()
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Header: Large Illustration
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 280.dp, max = 360.dp)
                    .padding(top = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.catalogweb),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(0.85f),
                    contentScale = ContentScale.Fit
                )
            }

            // Main Content Area
            OnboardingContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 24.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PublishCatalogOnboardingTablet(
    onContinueClick: () -> Unit,
    onLaterClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.publish_catalog_title),
                        fontWeight = FontWeight.Bold
                    )
                }, navigationIcon = {
                    IconButton(onClick = onCancelClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                })
        }
    ) { paddingValues ->

//    Surface(
//        modifier = modifier.fillMaxSize(),
//        color = MaterialTheme.colorScheme.surface
//    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Side: Illustration
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.catalogweb),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(0.9f),
                    contentScale = ContentScale.Fit
                )
            }

            // Right Side: Content + Pinned Buttons
            Column(
                modifier = Modifier
                    .weight(1.2f)
                    .fillMaxHeight()
                    .padding(horizontal = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OnboardingContent()
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Fixed Buttons
                OnboardingButtons(
                    onContinueClick = onContinueClick,
                    onLaterClick = onLaterClick,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
            }
        }
    }
}

@Composable
private fun OnboardingContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text(
            text = stringResource(R.string.publish_catalog_description),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 26.sp
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Benefits List
        BenefitsList()
    }
}

@Composable
private fun OnboardingButtons(
    onContinueClick: () -> Unit,
    onLaterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextButton(
            onClick = onLaterClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.publish_catalog_btn_later),
                style = MaterialTheme.typography.labelLarge.copy(
                    color = MaterialTheme.colorScheme.outline,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onContinueClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF37C8AB) // Brand primary color
            ),
            shape = MaterialTheme.shapes.large,
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
        ) {
            Text(
                text = stringResource(R.string.publish_catalog_btn_continue),
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
private fun BenefitsList() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        BenefitItem(
            icon = Icons.Outlined.Share,
            text = stringResource(R.string.publish_catalog_benefit_whatsapp)
        )
        BenefitItem(
            icon = Icons.Outlined.Sync,
            text = stringResource(R.string.publish_catalog_benefit_update)
        )
        BenefitItem(
            icon = Icons.Outlined.WebAssetOff,
            text = stringResource(R.string.publish_catalog_benefit_no_web)
        )
    }
}

@Composable
private fun BenefitItem(
    icon: ImageVector,
    text: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = Color(0xFF37C8AB).copy(alpha = 0.12f),
            modifier = Modifier.size(44.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF0A6B5A),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(20.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun PublishCatalogOnboardingPreview() {
    PuntroSalesDemoTheme {
        PublishCatalogOnboardingScreen(
            onContinueClick = {},
            onLaterClick = {},
            onCancelClick = {}
        )
    }
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun PublishCatalogOnboardingTabletPreview() {
    PuntroSalesDemoTheme {
        PublishCatalogOnboardingScreen(
            onContinueClick = {},
            onLaterClick = {},
            onCancelClick = {}
        )
    }
}
