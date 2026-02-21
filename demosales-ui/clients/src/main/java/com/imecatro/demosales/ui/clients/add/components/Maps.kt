package com.imecatro.demosales.ui.clients.add.components

import android.location.Geocoder
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun MapCard(
    address: String, // Dirección a geocodificar -> Example: Street Name #, CP, State
    onCameraMoveFinished: (LatLng) -> Unit
) {
    val context = LocalContext.current
    val geocoder = remember { Geocoder(context) }
    val cameraPositionState = rememberCameraPositionState {
        // Coordenadas por defecto (p. ej., Ciudad de México)
        position = CameraPosition.fromLatLngZoom(LatLng(19.4326, -99.1332), 12f)
    }

    val view = LocalView.current
    if (view.isInEditMode) return

    // Geocodifica la dirección de texto a LatLng y mueve la cámara allí
    LaunchedEffect(address) {
        // Un pequeño debounce para no geocodificar con cada letra que se escribe
        snapshotFlow { address }
            .debounce(1000) // Espera 1 segundo después de que el usuario deja de escribir
            .distinctUntilChanged()
            .collect { currentAddress ->
                if (currentAddress.isNotBlank()) {
                    try {
                        @Suppress("DEPRECATION")
                        val addressList = geocoder.getFromLocationName(currentAddress, 1)
                        if (addressList?.isNotEmpty() == true) {
                            val firstAddress = addressList[0]
                            val newPosition = LatLng(firstAddress.latitude, firstAddress.longitude)
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(
                                    newPosition,
                                    17f
                                )
                            )
                        }
                    } catch (e: Exception) {
                        Log.e("MapCard", "Error geocoding address: '$currentAddress'", e)
                    }
                }
            }
    }

    // Observa cuando el mapa deja de moverse para obtener la ubicación central.
    LaunchedEffect(cameraPositionState) {
        snapshotFlow { cameraPositionState.isMoving }
            .collect { isMoving ->
                if (!isMoving) {
                    val newLatLng = cameraPositionState.position.target
                    onCameraMoveFinished(newLatLng)
                }
            }
    }


    Box(Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraPositionState
        )
        // Icono de pin superpuesto en el centro del mapa
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Pin de ubicación",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.Center).size(40.dp)
        )
    }
}