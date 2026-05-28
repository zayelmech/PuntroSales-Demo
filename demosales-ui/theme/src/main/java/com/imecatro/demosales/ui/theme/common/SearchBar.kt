package com.imecatro.demosales.ui.theme.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SearchTopBar(
    title: String,
    query: String = "",
    onQueryChange: (String) -> Unit = {},
    onSearchAction: () -> Unit = {},
    onClearSearchBar: () -> Unit = {},
    placeholder: String = "Buscar",
    searchTrailingIcon: @Composable (() -> Unit)? = null,
    extraActions: @Composable RowScope.() -> Unit = {},
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    // Stay expanded if there's text (e.g. on configuration change)
    LaunchedEffect(query) {
        if (query.isNotEmpty()) isExpanded = true
    }

    AnimatedContent(
        targetState = isExpanded,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "SearchExpansion"
    ) { expanded ->
        if (expanded) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = {
                    isExpanded = false
                    onClearSearchBar()
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }

                OutlinedTextField(
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester),
                    shape = MaterialTheme.shapes.extraLarge,
                    value = query,
                    onValueChange = onQueryChange,
                    placeholder = { Text(placeholder) },
                    leadingIcon = {
                        if (query.isEmpty())
                            Icon(Icons.Default.Search, null)
                        else
                            IconButton(onClick = onClearSearchBar) {
                                Icon(Icons.Default.Clear, null)
                            }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions { onSearchAction() },
                    trailingIcon = searchTrailingIcon
                )
            }
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                extraActions()

                IconButton(onClick = { isExpanded = true }) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSearchTopBar() {
    SearchTopBar(
        title = "Products",
        query = "",
        onQueryChange = {},
        extraActions = {
            // Example of extra icons
        }
    )
}