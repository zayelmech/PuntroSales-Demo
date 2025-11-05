package com.imecatro.demosales.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Preview(showBackground = false)
@Composable
fun DropListPicker(
    list: List<String> = listOf(),
    itemSelected: String = "hey",
    onAddItem: (() -> Unit)? = null,
    onItemClicked: (String) -> Unit = {}
) {


    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .width(150.dp)
            .border(border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline), shape =  MaterialTheme.shapes.medium)
    ) {

        Row(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    OutlinedTextFieldDefaults.shape
                )
                .sizeIn(minWidth = 150.dp)
                .height(60.dp)
                .padding(10.dp)
                .clickable { expanded = true },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(itemSelected, style = MaterialTheme.typography.bodyMedium)
            Icon(imageVector = Icons.Filled.ArrowDropDown, null)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                //onItemClicked(list.first())
            },
            modifier = Modifier
                .width(150.dp)
        ) {
            list.forEach { name ->

                DropdownMenuItem(
                    text = { Text(name) },
                    onClick = {
                        expanded = false
                        //itemSelected = name
                        onItemClicked(name)
                    },
                    modifier = Modifier.padding(0.5.dp)
                )
                if (name != list.last()) {
                    HorizontalDivider()
                }

            }
            if (onAddItem != null) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.btn_add_item)) },
                    onClick = {
                        expanded = false
                        onAddItem()
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.padding(0.5.dp)
                )
            }
        }
    }
}
