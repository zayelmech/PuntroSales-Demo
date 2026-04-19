package com.imecatro.demosales.ui.clients.details.views
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import com.imecatro.demosales.ui.clients.R
import com.imecatro.demosales.ui.clients.details.model.ClientDetailsUiModel


@Preview(showBackground = true)
@Composable
internal fun ClientDetailsInfoScreen(
    clientDetails: ClientDetailsUiModel = ClientDetailsUiModel.dummy,
    onDeleteClicked: () -> Unit = {},
    onEditClicked: () -> Unit = {}
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(clientDetails.latitude, clientDetails.longitude),
            15f
        )
    }

    LaunchedEffect(clientDetails.latitude, clientDetails.longitude) {
        cameraPositionState.move(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(clientDetails.latitude, clientDetails.longitude),
                15f
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current)
                    .placeholder(R.drawable.baseline_mood_24)
                    .error(R.drawable.baseline_mood_24)
                    .data(clientDetails.imageUri)
                    .crossfade(true)
                    .build()
            ),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .requiredSizeIn(maxHeight = 280.dp),
            contentScale = ContentScale.FillWidth
        )
        Spacer(modifier = Modifier.height(20.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp, 10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            //Client name
            Text(
                text = clientDetails.clientName,
                style = MaterialTheme.typography.titleMedium
            )

            //Phone Number
            SelectionContainer {
                Text(
                    text = clientDetails.phoneNumber,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            //Details
            Text(
                text = stringResource(R.string.txt_address),
                style = MaterialTheme.typography.titleSmall
            )
            HorizontalDivider()
            SelectionContainer {
                Text(
                    text = clientDetails.clientAddress,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        GoogleMap(
            modifier = Modifier
                .sizeIn(maxWidth = 400.dp)
                .height(200.dp)
                .fillMaxWidth(),
            cameraPositionState = cameraPositionState
        ) {
            Marker(
                state = rememberUpdatedMarkerState(
                    position = LatLng(
                        clientDetails.latitude,
                        clientDetails.longitude
                    )
                ),
                title = clientDetails.clientName
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .sizeIn(maxWidth = 400.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            OutlinedButton(
                modifier = Modifier.weight(1f),

                onClick = onDeleteClicked,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Filled.Delete, null)
                Text(text = stringResource(R.string.btn_delete))
            }

            Button(
                modifier = Modifier.weight(1f),

                onClick = onEditClicked
            ) {
                Icon(Icons.Filled.Edit, null)
                Text(text = stringResource(R.string.btn_edit))
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
    }
}
