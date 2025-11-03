package com.imecatro.demosales.ui.sales.add.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ScannerToggle(
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val bg = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val fg = if (enabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        color = bg,
        contentColor = fg,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = if (enabled) 2.dp else 1.dp,
        modifier = Modifier
            .wrapContentWidth()
            .heightIn(min = 52.dp)
            .clickable { onToggle(!enabled) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Icon(
                imageVector = if (enabled) Icons.Outlined.Clear else Icons.Outlined.Check,
                contentDescription = null
            )
            Spacer(Modifier.width(8.dp))
            Text(if (enabled) "Modo escáner: ON" else "Modo escáner: OFF")
        }
    }
}
