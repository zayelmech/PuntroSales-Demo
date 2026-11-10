package com.imecatro.demosales.ui.sales.list.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.imecatro.demosales.ui.sales.R
import com.imecatro.demosales.ui.sales.list.model.SalesDateRange
import com.imecatro.demosales.ui.sales.list.model.toPickerDate
import com.imecatro.demosales.ui.sales.list.model.toPickerMillis

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SalesDateFilterDialog(
    range: SalesDateRange?,
    onApply: (SalesDateRange?) -> Unit,
    onDismiss: () -> Unit
) {
    var mode by rememberSaveable { mutableIntStateOf(0) }
    when (mode) {
        0 -> AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(stringResource(R.string.txt_filter_date)) },
            text = {
                Column {
                    TextButton(onClick = { mode = 1 }) {
                        Text(stringResource(R.string.sales_date_single))
                    }
                    TextButton(onClick = { mode = 2 }) {
                        Text(stringResource(R.string.sales_date_range))
                    }
                    TextButton(onClick = { onApply(null) }) {
                        Text(stringResource(R.string.sales_date_all))
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = onDismiss) { Text(stringResource(R.string.sales_date_cancel)) }
            }
        )
        1 -> {
            val state = rememberDatePickerState(initialSelectedDateMillis = range?.start?.toPickerMillis())
            DatePickerDialog(
                onDismissRequest = onDismiss,
                confirmButton = {
                    TextButton(
                        enabled = state.selectedDateMillis != null,
                        onClick = {
                            state.selectedDateMillis?.let { onApply(SalesDateRange(it.toPickerDate())) }
                        }
                    ) { Text(stringResource(R.string.sales_date_apply)) }
                },
                dismissButton = {
                    TextButton(onClick = onDismiss) { Text(stringResource(R.string.sales_date_cancel)) }
                }
            ) { DatePicker(state = state) }
        }
        2 -> {
            val state = rememberDateRangePickerState(
                initialSelectedStartDateMillis = range?.start?.toPickerMillis(),
                initialSelectedEndDateMillis = range?.end?.toPickerMillis()
            )
            DatePickerDialog(
                onDismissRequest = onDismiss,
                confirmButton = {
                    TextButton(
                        enabled = state.selectedStartDateMillis != null && state.selectedEndDateMillis != null,
                        onClick = {
                            val start = state.selectedStartDateMillis
                            val end = state.selectedEndDateMillis
                            if (start != null && end != null) {
                                onApply(SalesDateRange(start.toPickerDate(), end.toPickerDate()))
                            }
                        }
                    ) { Text(stringResource(R.string.sales_date_apply)) }
                },
                dismissButton = {
                    TextButton(onClick = onDismiss) { Text(stringResource(R.string.sales_date_cancel)) }
                }
            ) {
                DateRangePicker(state = state, modifier = Modifier.fillMaxWidth().height(500.dp))
            }
        }
    }
}
