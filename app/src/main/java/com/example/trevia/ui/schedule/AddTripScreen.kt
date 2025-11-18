package com.example.trevia.ui.schedule

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trevia.R
import com.example.trevia.domain.schedule.usecase.AddTripResult
import com.example.trevia.utils.toDateString
import com.example.trevia.ui.utils.DateRangePickerModal
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun AddTripScreen(addTripViewModel: AddTripViewModel = hiltViewModel())
{
    val addTripUiState by remember { addTripViewModel.addTripUiState }
    val context = LocalContext.current
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(dimensionResource(R.dimen.padding_medium)),
        ) {
            //行程名称
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = dimensionResource(R.dimen.padding_small),
                        bottom = dimensionResource(R.dimen.padding_small)
                    )
            ) {
                Text(
                    text = stringResource(R.string.trip_name_label),
                    style = MaterialTheme.typography.titleMedium
                )
                TextField(
                    value = addTripUiState.tripName,
                    onValueChange = { addTripViewModel.updateAddTripUiState { copy(tripName = it) } },
                    placeholder = { Text(stringResource(R.string.trip_name_placeholder)) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = stringResource(R.string.destination_search_icon)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
            }
            // 行程目的地
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = dimensionResource(R.dimen.padding_small),
                        bottom = dimensionResource(R.dimen.padding_small)
                    )
            ) {
                Text(
                    text = stringResource(R.string.destination_label),
                    style = MaterialTheme.typography.titleMedium
                )
                TextField(
                    value = addTripUiState.tripLocation,
                    onValueChange = { addTripViewModel.updateAddTripUiState { copy(tripLocation = it) } },
                    placeholder = { Text(stringResource(R.string.enter_destination_placeholder)) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = stringResource(R.string.destination_search_icon)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
            }
            // 行程时间
            TripDatePicker(
                addTripViewModel = addTripViewModel,
                tripDateRange = addTripUiState.tripDateRange,
                onTripDateChange = {
                    addTripViewModel.updateAddTripUiState { copy(tripDateRange = it) }
                })

            Button(
                onClick = {
                    addTripViewModel.saveTrip()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_medium))
            ) {
                Text(stringResource(R.string.add_trip_button))
            }
            LaunchedEffect(addTripUiState.saveTripResult) {
                when (addTripUiState.saveTripResult)
                {
                    is AddTripResult.Success       ->
                    {
                        Toast.makeText(context, R.string.save_trip_success, Toast.LENGTH_SHORT)
                            .show()
//                    navigateBack()
                        addTripViewModel.clearSaveTripResult()
                    }

                    is AddTripResult.InvalidData   ->
                    {
                        Toast.makeText(
                            context,
                            R.string.invalid_trip_data,
                            Toast.LENGTH_SHORT
                        ).show()
                        addTripViewModel.clearSaveTripResult()
                    }

                    is AddTripResult.DatabaseError ->
                    {
                        Toast.makeText(
                            context,
                            R.string.save_trip_failed,
                            Toast.LENGTH_SHORT
                        ).show()
                        addTripViewModel.clearSaveTripResult()
                    }

                    null                           -> Unit
                }
            }
        }
    }
}

@Composable
private fun TripDatePicker(
    addTripViewModel: AddTripViewModel,
    tripDateRange: String,
    onTripDateChange: (String) -> Unit
)
{
    var showDateRangePicker by remember { mutableStateOf(false) }

    // 点击 TextField 弹出日期选择器
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = dimensionResource(R.dimen.padding_small),
                bottom = dimensionResource(R.dimen.padding_small)
            )
    ) {
        Text(
            text = stringResource(R.string.date_label),
            style = MaterialTheme.typography.titleMedium
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDateRangePicker = true } // 点击 Box 弹出日期选择器
        ) {
            OutlinedTextField(
                value = tripDateRange,
                onValueChange = {},
                readOnly = true,
                enabled = false, // 禁止内部输入，只能通过点击弹窗
                leadingIcon = {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = stringResource(R.string.date_picker_icon)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.choose_date_placeholder)) },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledContainerColor = OutlinedTextFieldDefaults.colors().focusedContainerColor,
                    disabledTextColor = OutlinedTextFieldDefaults.colors().focusedTextColor,
                    disabledLabelColor = OutlinedTextFieldDefaults.colors().focusedLabelColor,
                    disabledLeadingIconColor = OutlinedTextFieldDefaults.colors().focusedLeadingIconColor,
                    disabledTrailingIconColor = OutlinedTextFieldDefaults.colors().focusedTrailingIconColor,
                    disabledPlaceholderColor = OutlinedTextFieldDefaults.colors().focusedPlaceholderColor,
                    disabledBorderColor = OutlinedTextFieldDefaults.colors().unfocusedIndicatorColor,
                )
            )
        }
    }

    // 弹出日期范围选择器
    if (showDateRangePicker)
    {
        DateRangePickerModal(
            onDateRangeSelected = { range ->
                val dateText = addTripViewModel.selectedDateRangeToString(range)
                onTripDateChange(dateText)
                showDateRangePicker = false
            },
            onDismiss = { showDateRangePicker = false }
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun AddTripScreenPreview()
{
    AddTripScreen()
}
