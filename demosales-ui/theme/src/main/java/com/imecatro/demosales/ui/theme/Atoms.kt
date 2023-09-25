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

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun TextFieldCompose(
    value: String = "Write here!",
    listener: (String) -> Unit = {},
    prompt: String = ""
) {

    OutlinedTextField(
        value = value,
        onValueChange = { listener(it) },
        label = {
            Text(
                text = prompt,
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        modifier = Modifier
            .height(60.dp),
        shape = RoundedCornerShape(2),
        singleLine = true
    )
}

@Preview(showBackground = true)
@Composable
fun ButtonFancy(
    text: String = "Click here!",
    color: Color = BlueTurquoise80,
    paddingX: Dp = 10.dp,
    icon: ImageVector? = Icons.Filled.Edit,
    enable: Boolean = true,
    onClicked: () -> Unit = {}
) {
    Button(
        onClick = { onClicked() },
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingX, 10.dp),
        colors = ButtonDefaults.buttonColors(color),
        shape = RoundedCornerShape(50),
        enabled = enable
    ) {
        icon?.let { imageVector ->
            Icon(
                imageVector = imageVector,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.padding(4.dp)
            )
        }
        Text(text = text, style = Typography.titleMedium, color = Color.White)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun DropListPicker(
    list: List<String> = listOf(),
    itemSelected: String = "hey",
    // expanded: Boolean = false,
//    onExpanded: () -> Unit,
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
                onItemClicked(list.first())
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
                    Divider()
                }

            }
        }
    }
}
