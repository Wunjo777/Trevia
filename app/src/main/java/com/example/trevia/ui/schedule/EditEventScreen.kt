package com.example.trevia.ui.schedule

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.trevia.ui.utils.AdvancedTimePicker
import com.example.trevia.utils.toLocalTime
import java.time.LocalTime

@Composable
fun EditEventScreen(
    navigateBack: () -> Unit,
    editEventViewModel: EditEventViewModel = hiltViewModel()
)
{
    val editEventUiState by remember { editEventViewModel.editEventUiState }
    val context = LocalContext.current

    when (editEventUiState)
    {
        is EditEventUiState.Loading ->
        {
            CircularProgressIndicator()
        }

        is EditEventUiState.NotFound ->
        {
            Text(text = "加载事件失败")
        }

        is EditEventUiState.Success ->
        {
            EditEventContent(
                onEventInfoChange = editEventViewModel::updateEventInfo,
                onSaveButtonClick = editEventViewModel::updateEvent,
                navigateBack = navigateBack,
                editEventUiState = editEventUiState as EditEventUiState.Success,
                context= context
            )
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEventContent(
    onEventInfoChange: (EventInfoUiState.() -> EventInfoUiState) -> Unit,
    onSaveButtonClick: ((Boolean) -> Unit) -> Unit,
    navigateBack: () -> Unit,
    editEventUiState: EditEventUiState.Success,
    context: Context
)
{
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "编辑事件") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {

            // =========== 1. 地点信息 Card ===========
            Text(text = "地点", style = MaterialTheme.typography.titleMedium)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = editEventUiState.eventInfoUiState.location.ifEmpty { "暂无地点信息" })
                    if (editEventUiState.eventInfoUiState.address.isNotEmpty())
                    {
                        Text(
                            text = editEventUiState.eventInfoUiState.address,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))


            // =========== 2. 选择时间段 ===========
            Text(text = "时间", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                // Start Time
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showStartPicker = true }
                ) {
                    TextField(
                        value = editEventUiState.eventInfoUiState.startTime,
                        onValueChange = {},
                        placeholder = { Text("开始时间，例如 09:00") },
                        leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                        singleLine = true,
                        readOnly = true,
                        enabled = false
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                // End Time
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showEndPicker = true }
                ) {
                    TextField(
                        value = editEventUiState.eventInfoUiState.endTime,
                        onValueChange = {},
                        placeholder = { Text("结束时间，例如 10:30") },
                        leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                        singleLine = true,
                        readOnly = true,
                        enabled = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))


            // =========== 3. 备注输入框 ===========
            Text(text = "备注", style = MaterialTheme.typography.titleMedium)
            TextField(
                value = editEventUiState.eventInfoUiState.description,
                onValueChange = { newDescription ->
                    onEventInfoChange { copy(description = newDescription) }
                },
                placeholder = { Text("关于该事件的备注…") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))


            // =========== 4. 保存按钮 ===========
            Button(
                onClick = {
                    onSaveButtonClick { success ->
                        if (success)
                        {
                            Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show()
                            navigateBack()
                        }
                        else
                        {
                            Toast.makeText(context, "保存失败", Toast.LENGTH_SHORT).show()
                        }
                    }

                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("保存")
            }
        }
        if (showStartPicker)
        {
            AdvancedTimePicker(
                onConfirm = { timeState ->
                    onEventInfoChange {
                        copy(
                            startTime = LocalTime.of(
                                timeState.hour,
                                timeState.minute
                            ).toString()
                        )
                    }
                    showStartPicker = false
                },
                onDismiss = { showStartPicker = false }
            )
        }
        if (showEndPicker)
        {
            AdvancedTimePicker(
                onConfirm = { timeState ->
                    if (LocalTime.of(timeState.hour, timeState.minute) <=
                        editEventUiState.eventInfoUiState.startTime.toLocalTime()
                    )
                    {
                        Toast.makeText(
                            context,
                            "结束时间不能早于开始时间",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else
                    {
                        onEventInfoChange {
                            copy(
                                endTime = LocalTime.of(timeState.hour, timeState.minute).toString()
                            )
                        }
                        showEndPicker = false
                    }
                },
                onDismiss = { showEndPicker = false }
            )
        }
    }
}