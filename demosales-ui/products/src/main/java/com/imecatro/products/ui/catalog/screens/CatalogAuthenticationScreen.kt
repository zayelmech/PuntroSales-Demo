package com.imecatro.products.ui.catalog.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imecatro.products.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogAuthenticationScreen(
    onBackClick: () -> Unit,
    onGoogleSignInClick: () -> Unit,
    onContinueClick: () -> Unit,
    isUserAuthenticated: Boolean,
    userAlias: String,
    onChangeAccount: () -> Unit
) {
    var isAccepted by remember { mutableStateOf(false) }
    val annotatedString = buildAnnotatedString {
        append(stringResource(R.string.catalog_auth_accept_terms_prefix))
        val linkStyles = TextLinkStyles(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline,
                fontWeight = FontWeight.Bold
            )
        )
        withLink(LinkAnnotation.Url("https://www.imecatro.com/termsconditions.html", linkStyles)) {
            append(stringResource(R.string.catalog_auth_terms_link))
        }
        append(stringResource(R.string.catalog_auth_accept_terms_middle))
        withLink(LinkAnnotation.Url("https://www.imecatro.com/privacy.html", linkStyles)) {
            append(stringResource(R.string.catalog_auth_privacy_link))
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.catalog_auth_title),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.catalog_auth_back_desc)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_publish_catalog),
                    contentDescription = null,
                    modifier = Modifier.size(200.dp),
                    tint = Color.Unspecified
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = if (isUserAuthenticated) "Hi! $userAlias" else stringResource(R.string.catalog_auth_headline),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isUserAuthenticated) {
                    stringResource(R.string.catalog_auth_signed)
                } else {
                    stringResource(R.string.catalog_auth_description)
                },
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isAccepted,
                    onCheckedChange = { isAccepted = it }
                )
                Text(
                    text = annotatedString,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (isUserAuthenticated) {

                OutlinedButton(
                    onClick = onChangeAccount,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(text = "Change Account")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onContinueClick,
                    enabled = isAccepted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(text = "Continue")
                }

            } else {
                TextButton(
                    onClick = onBackClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.btn_skip_auth),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onGoogleSignInClick,
                    enabled = isAccepted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = MaterialTheme.shapes.medium,
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    ),
                    border = ButtonDefaults.outlinedButtonBorder(enabled = isAccepted)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_google_logo),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.btn_sign_in_google),
                        style = MaterialTheme.typography.labelLarge.copy(fontSize = 16.sp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CatalogAuthenticationScreenPreview() {
    CatalogAuthenticationScreen(
        onBackClick = {},
        onGoogleSignInClick = {},
        onContinueClick = {},
        isUserAuthenticated = true,
        userAlias = "Abdiel",
        onChangeAccount = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun CatalogAuthenticationScreenPreviewx() {
    CatalogAuthenticationScreen(
        onBackClick = {},
        onGoogleSignInClick = {},
        onContinueClick = {},
        isUserAuthenticated = false,
        userAlias = "Abdiel",
        onChangeAccount = {}
    )
}
