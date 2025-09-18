package com.imecatro.demosales.ui.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Preview(showBackground = true)
@Composable
fun DropListPicker(
    list: List<String> = listOf(),
    itemSelected: String = "hey",
    onAddItem : (() -> Unit)? = null,
    onItemClicked: (String) -> Unit = {}
) {


    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .width(150.dp)
        //.height(120.dp)
        // .wrapContentSize(Alignment.Center)
    ) {
        OutlinedTextField(
            value = itemSelected,
            onValueChange = {},
            readOnly = true,
            enabled = false,
            modifier = Modifier
                .width(150.dp)
                .height(60.dp)
                .clickable {
                    expanded = true
//                    onExpanded()
                },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null
                )
            }
        )

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
                    text = { Text("Add") },
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
