package com.imecatro.products.ui.catalog.screens

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme
import com.imecatro.products.ui.catalog.state.WebCatalogManagementState

val PuntroSalesPrimary: Color
    @Composable get() = MaterialTheme.colorScheme.primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebCatalogManagementScreen(
    state: WebCatalogManagementState,
    onBack: () -> Unit,
    onViewCatalog: () -> Unit,
    onShareCatalog: () -> Unit,
    onUpdateCatalog: () -> Unit,
    onCopyLink: () -> Unit,
    onViewQr: () -> Unit,
    onUnpublishCatalog: () -> Unit,
    onHelp: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Catálogo Web",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Más opciones")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Copiar enlace") },
                                onClick = { onCopyLink(); showMenu = false },
                                leadingIcon = { Icon(Icons.Outlined.ContentCopy, null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Ver código QR") },
                                onClick = { onViewQr(); showMenu = false },
                                leadingIcon = { Icon(Icons.Outlined.QrCode, null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Despublicar catálogo") },
                                onClick = { onUnpublishCatalog(); showMenu = false },
                                leadingIcon = { Icon(Icons.Outlined.PublicOff, null) },
                                colors = MenuDefaults.itemColors(textColor = MaterialTheme.colorScheme.error)
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Ayuda") },
                                onClick = { onHelp(); showMenu = false },
                                leadingIcon = { Icon(Icons.AutoMirrored.Outlined.HelpOutline, null) }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            BottomPersistentActions(
                onUpdate = onUpdateCatalog,
                onShare = onShareCatalog
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header Description
            Text(
                text = "Tu catálogo está publicado y disponible para tus clientes.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Status Card
            CatalogStatusCard(
                name = state.catalogName,
                url = state.catalogUrl,
                lastUpdate = state.lastUpdate,
                onCopyLink = onCopyLink
            )

            // Quick Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionButton(
                    label = "Ver catálogo",
                    icon = Icons.Outlined.Visibility,
                    onClick = onViewCatalog,
                    modifier = Modifier.weight(1f)
                )
                QuickActionButton(
                    label = "Compartir",
                    icon = Icons.Outlined.Share,
                    onClick = onShareCatalog,
                    modifier = Modifier.weight(1f)
                )
                QuickActionButton(
                    label = "Actualizar",
                    icon = Icons.Outlined.Refresh,
                    onClick = onUpdateCatalog,
                    modifier = Modifier.weight(1f),
                    highlight = true
                )
            }

            // Summary Section
            SummarySection(
                productsCount = state.productsCount.toString(),
                categoriesCount = state.categoriesCount.toString(),
                imagesCount = state.imagesCount.toString(),
                visitsCount = state.visitsCount.toString()
            )

            // Activity Section
//            ActivitySection(
//                lastPublicationDate = state.lastPublicationDate,
//                lastUpdateDate = state.lastUpdateDate,
//                recentlyAddedCount = state.recentlyAddedCount.toString(),
//                recentlyModifiedCount = state.recentlyModifiedCount.toString()
//            )

            // Info Footer Card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = PuntroSalesPrimary.copy(alpha = 0.05f)
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, PuntroSalesPrimary.copy(alpha = 0.1f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Outlined.Info,
                        contentDescription = null,
                        tint = PuntroSalesPrimary
                    )
                    Text(
                        text = "Puedes actualizar tu catálogo en cualquier momento. Los cambios se reflejarán automáticamente para tus clientes.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(100.dp)) // Extra space for FAB/BottomBar
        }
    }
}

@Composable
fun CatalogStatusCard(
    name: String,
    url: String,
    lastUpdate: String,
    onCopyLink: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4CAF50)) // Green
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Publicado",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Última actualización: $lastUpdate",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                onClick = onCopyLink
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.Link,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = PuntroSalesPrimary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = url,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = onCopyLink) {
                        Text("Copiar", color = PuntroSalesPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    highlight: Boolean = false
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            shape = RoundedCornerShape(20.dp),
            color = if (highlight) PuntroSalesPrimary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface,
            border = BorderStroke(
                1.dp,
                if (highlight) PuntroSalesPrimary else MaterialTheme.colorScheme.outlineVariant
            ),
            onClick = onClick
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (highlight) PuntroSalesPrimary else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SummarySection(
    productsCount: String,
    categoriesCount: String,
    imagesCount: String,
    visitsCount: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Resumen",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                value = productsCount,
                label = "Productos",
                icon = Icons.Outlined.Inventory2,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                value = categoriesCount,
                label = "Categorías",
                icon = Icons.Outlined.Category,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                value = imagesCount,
                label = "Imágenes",
                icon = Icons.Outlined.Image,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                value = visitsCount,
                label = "Visitas",
                icon = Icons.AutoMirrored.Outlined.TrendingUp,
                modifier = Modifier.weight(1f),
                isBlurred = true
            )
        }
    }
}

@Composable
fun StatCard(
    value: String,
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    isBlurred: Boolean = false
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.outline
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = if (isBlurred) Modifier.blur(7.dp) else Modifier
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ActivitySection(
    lastPublicationDate: String,
    lastUpdateDate: String,
    recentlyAddedCount: String,
    recentlyModifiedCount: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Últimos cambios",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ActivityItem("Última publicación", lastPublicationDate)
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                ActivityItem("Última actualización", lastUpdateDate)
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                ActivityItem("Productos agregados", "$recentlyAddedCount recientemente")
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                ActivityItem("Productos modificados", "$recentlyModifiedCount recientemente")
            }
        }
    }
}

@Composable
fun ActivityItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun BottomPersistentActions(
    onUpdate: () -> Unit,
    onShare: () -> Unit
) {
    Surface(
        tonalElevation = 8.dp,
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onUpdate,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PuntroSalesPrimary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Actualizar catálogo",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            OutlinedButton(
                onClick = onShare,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, PuntroSalesPrimary),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PuntroSalesPrimary)
            ) {
                Icon(Icons.Default.Share, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Compartir catálogo",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Light Mode",
    device = "spec:width=1080px,height=2340px,dpi=480"
)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
fun WebCatalogManagementScreenPreview() {
    PuntroSalesDemoTheme {
        WebCatalogManagementScreen(
            state = WebCatalogManagementState.idle.copy(
                catalogName = "Mi Tienda",
                catalogUrl = "https://apps.imecatro.com/c/123456",
                isPublished = true
            ),
            onBack = {},
            onViewCatalog = {},
            onShareCatalog = {},
            onUpdateCatalog = {},
            onCopyLink = {},
            onViewQr = {},
            onUnpublishCatalog = {},
            onHelp = {}
        )
    }
}
