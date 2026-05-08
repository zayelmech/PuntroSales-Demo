package com.imecatro.demosales

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.imecatro.demosales.ui.clients.list.model.ClientUiModel
import com.imecatro.demosales.ui.clients.list.views.ListOfClients
import com.imecatro.demosales.ui.clients.list.views.SyncOptionsDialog
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme

@Preview(showBackground = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL, locale = "es"
)
@Composable
private fun MarketingClients() {
    PuntroSalesDemoTheme{
        Surface(color = MaterialTheme.colorScheme.background) {

            Box() {
                ListOfClients (list = getDummyList())
                SyncOptionsDialog(
                    onDismissRequest = {},
                    onSyncAll = {},
                    onSelectSpecific = { }
                )
            }
        }
    }
}


private fun getDummyList(): List<ClientUiModel> = listOf(
    ClientUiModel(1, "Juan Pérez", "555-0101", "", "Av. Principal 123"),
    ClientUiModel(2, "María García", "555-0202", "", "Calle Juárez 456"),
    ClientUiModel(3, "Carlos Rodríguez", "555-0303", "", "Blvd. Revolución 789"),
    ClientUiModel(4, "Ana Martínez", "555-0404", "", "Privada Madero 101"),
    ClientUiModel(5, "Luis Hernández", "555-0505", "", "Andador Zapata 202"),
    ClientUiModel(6, "Sofía López", "555-0606", "", "Calzada Independencia 303"),
    ClientUiModel(7, "Diego Sánchez", "555-0707", "", "Avenida de los Insurgentes 404"),
    ClientUiModel(8, "Lucía Torres", "555-0808", "", "Paseo de la Reforma 505"),
    ClientUiModel(9, "Roberto Flores", "555-0909", "", "Calle Hidalgo 606"),
    ClientUiModel(10, "Elena Ramírez", "555-1010", "", "Boulevard Kino 707")
)