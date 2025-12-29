package com.example.trevia.ui.schedule.TripDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.trevia.R

@Composable
fun ExpandedSheetContent(
    selectedDayId: Long?,
    tripDetailUiState: TripDetailUiState.Success,
    onDeleteEvent: (Long) -> Unit,
    onClickEvent: (Long) -> Unit,
    onLocationClick: (String,String, String, Double, Double) -> Unit,
    onSelectedDayChange: (Long?) -> Unit,
    onAddEventChange: (Boolean) -> Unit
)
{
    val selectedDay = if (selectedDayId == null) null
    else tripDetailUiState.days.firstOrNull { it.dayId == selectedDayId }


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f)
            .navigationBarsPadding()
    ) {
        // ------- 主体内容 -------
        Column {
            TripInfoContent(
                tripDetailUiState.tripName,
                tripDetailUiState.tripLocation,
                tripDetailUiState.tripDateRange,
                tripDetailUiState.days.count()
            )
            DaysNavigator(
                tripDetailUiState.days,
                selectedDayId = selectedDayId,
                onSelectedChange = onSelectedDayChange
            )

            if (selectedDayId == null)
            {
                DayList(
                    tripDetailUiState.days,
                    onDayClicked = onSelectedDayChange
                )
            }
            else
            {
                if (selectedDay!!.events.isNotEmpty())
                {
                    EventList(
                        selectedDay.events,
                        onDeleteEvent = onDeleteEvent,
                        onClickEvent = onClickEvent,
                        onLocationClick = onLocationClick
                    )
                }
                else
                {
                    Text(
                        "暂无当天事件, 请添加事件，第${selectedDay.indexInTrip}天",
                        modifier = Modifier.padding(16.dp),
                        color = Color.Gray
                    )
                }
            }
        }

        // ------- FAB（条件显示） -------
        if (selectedDayId != null)// 总览不显示按钮
        {
            FloatingActionButton(
                onClick = { onAddEventChange(true) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "添加事件")
            }
        }
    }
}

@Composable
fun TripInfoContent(
    tripName: String,
    tripLocation: String,
    tripDateRange: String,
    tripDaysCount: Int
)
{
    Column(
        modifier = Modifier.padding(dimensionResource(R.dimen.padding_small)),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            tripName,
            style = MaterialTheme.typography.headlineMedium,
            maxLines = 1
        )
        Text(
            tripLocation,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.DarkGray,
            maxLines = 1
        )
        Row(verticalAlignment = Alignment.CenterVertically)
        {
            Text(
                text = tripDateRange,
                maxLines = 1,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray,
                modifier = Modifier.alignByBaseline()
            )
            VerticalDivider(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .width(2.dp)
                    .height(18.dp)
            )
            Text(
                stringResource(
                    R.string.trip_days_count,
                    tripDaysCount,
                    tripDaysCount - 1// 晚的数量 = 天的数量 - 1
                ),
                maxLines = 1,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray,
                modifier = Modifier.alignByBaseline()
            )
        }
    }
}

@Composable
fun DaysNavigator(
    days: List<DayWithEventsUiState>,
    selectedDayId: Long?,
    onSelectedChange: (Long?) -> Unit      // 点击回调
)
{
    val scrollState = rememberScrollState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. 总览按钮
        DayNavButton(
            label = "总览",
            isSelected = selectedDayId == null,
            onClick = {
                onSelectedChange(null)
            }
        )

        // 2. 分隔符
        VerticalDivider(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .width(3.dp)
                .height(36.dp)
                .background(Color.Black)
        )

        // 3. 每天按钮
        for (day in days)
        {
            DayNavButton(
                label = day.indexInTrip.toString(),
                isSelected = selectedDayId == day.dayId,
                onClick = {
                    onSelectedChange(day.dayId)
                }
            )
        }
    }
}

@Composable
fun DayNavButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
)
{
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(if (isSelected) Color.DarkGray else Color.Transparent)
            .border(
                width = 2.dp,
                color = Color.Black,
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else Color.Black,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}