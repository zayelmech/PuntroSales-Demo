package com.imecatro.products.ui.catalog.screens

import android.content.Intent
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView


@Composable
fun HtmlWebView(
    html: String,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var lastHtml by remember { mutableStateOf<String?>(null) }

    // Crea y conserva una sola instancia de WebView
    val webView = remember {
        WebView(context).apply {
            settings.javaScriptEnabled = false
            settings.domStorageEnabled = true
            settings.loadsImagesAutomatically = true
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
            // Abre links http/https en el navegador externo

            // Add these two lines to zoom out by default
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true

            setInitialScale(1)

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    request: WebResourceRequest
                ): Boolean {
                    val uri: Uri = request.url
                    if (uri.scheme?.startsWith("http") == true) {
                        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                        return true
                    }
                    return false
                }
            }
        }
    }

    DisposableEffect(webView) {
        onDispose { webView.destroy() }
    }

    AndroidView(
        modifier = modifier,
        factory = { webView },
        update = {
            // Evita recargar si el HTML no cambió
            if (lastHtml != html) {
                it.loadDataWithBaseURL(
                    /* baseUrl = */ "file:///android_asset/", // <--- FIX
                    /* data    = */ html,
                    /* mime    = */ "text/html",
                    /* encoding*/ "UTF-8",
                    /* history = */ null
                )
                lastHtml = html
            }
        }
    )
}