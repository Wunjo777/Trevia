package com.example.trevia.ui.schedule

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.trevia.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.E

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailScreen(
    navigateBack: () -> Unit,
    tripDetailViewModel: TripDetailViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
)
{
    val tripDetailUiState by tripDetailViewModel.tripDetailUiState.collectAsState()
    var isAddingEvent by remember { mutableStateOf(false) }
    val scaffoldState = rememberBottomSheetScaffoldState()

    BottomSheetScaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(
                        onClick = navigateBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.navigate_back)
                        )
                    }
                }
            )
        },
        scaffoldState = scaffoldState,
        sheetPeekHeight = 128.dp,//sheet折叠后高度
        sheetContent = {
            when (tripDetailUiState)
            {
                is TripDetailUiState.Loading ->
                {
                    Text("NotImplementedYet")
                }

                is TripDetailUiState.NotFound ->
                {
                    Text(stringResource(R.string.trip_not_found))
                }

                is TripDetailUiState.Success ->
                {
                    TripDetailSheetContent(
                        tripDetailUiState = tripDetailUiState as TripDetailUiState.Success,
                        scaffoldState = scaffoldState,
                        onAddEventChange = { isAddingEvent = it }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .background(Color(0xFFB3E5FC))
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Text("Map Here")
        }
    }

    if (isAddingEvent)
    {
        LocationSearchContent(onAddEventChange = { isAddingEvent = it })
    }
}

@Composable
fun LocationSearchContent(onAddEventChange: (Boolean) -> Unit)
{
    // 背景变暗
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f))
            .clickable { onAddEventChange(false) }
    ) {

        // 搜索栏
        SearchLocationBar(
            onClose = { onAddEventChange(false) },
            modifier = Modifier.align(
                Alignment.BottomCenter
            )
        )
    }
}

@Composable
fun SearchLocationBar(onClose: () -> Unit, modifier: Modifier = Modifier)
{
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    // 自动弹出输入法
    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
        keyboard?.show()
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {

                TextField(
                    value = "",
                    onValueChange = { /* TODO 搜索地点 */ },
                    placeholder = { Text(stringResource(R.string.search_location)) },
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester)
                )

                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = stringResource(R.string.close))
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailSheetContent(
    tripDetailUiState: TripDetailUiState.Success,
    scaffoldState: BottomSheetScaffoldState,
    onAddEventChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
)
{
    val isExpanded = scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded
    Crossfade(
        targetState = isExpanded,
        animationSpec = tween(
            durationMillis = 150,  // 动画时长 150ms
            easing = FastOutSlowInEasing // 插值器
        )
    ) { expanded ->
        if (expanded)
        {
            ExpandedSheetContent(tripDetailUiState, onAddEventChange)
        }
        else
        {
            FoldedSheetContent(tripDetailUiState)
        }
    }
}

@Composable
fun ExpandedSheetContent(
    tripDetailUiState: TripDetailUiState.Success,
    onAddEventChange: (Boolean) -> Unit
)
{
    var selectedIndex by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f)
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
                tripDetailUiState.days.count(),
                selectedIndex = selectedIndex,
                onSelectedChange = { selectedIndex = it }
            )

            if (selectedIndex == 0)
            {
                DayList(
                    tripDetailUiState.days,
                    onDayClicked = { selectedIndex = it }
                )
            }
            else
            {
                val selectedDay = tripDetailUiState.days[selectedIndex - 1]

                if (selectedDay.events.isNotEmpty())
                {
                    EventList(selectedDay.events)
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
        if (selectedIndex != 0)
        {  // 总览不显示按钮
            val selectedDay = tripDetailUiState.days[selectedIndex - 1]

            if (selectedDay.events.isEmpty())
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
}


@Composable
fun DayList(days: List<DayWithEventsUiState>, onDayClicked: (Int) -> Unit)
{
    LazyColumn(
        modifier = Modifier
    )
    {
        items(items = days, key = { day -> day.dayId })
        { day ->
            DayItem(indexInTrip = day.indexInTrip, onClick = { onDayClicked(it) })
        }
    }
}

@Composable
fun EventList(events: List<EventUiState>)
{
    LazyColumn(
        modifier = Modifier
    )
    {
        items(items = events, key = { event -> event.eventId })
        { event ->
            EventItem(event)
        }
    }
}

@Composable
fun EventItem(event: EventUiState)
{
    Column()
    {
        Text("NotImplementedYet")
    }
}

@Composable
fun DayItem(
    indexInTrip: Int,
    onClick: (Int) -> Unit
)
{
    Column(
        modifier = Modifier
            .padding(dimensionResource(R.dimen.padding_small))
            .fillMaxWidth()
    )
    {
        Text(
            "Day $indexInTrip",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick(indexInTrip) }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_small)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("NotImplementedYet")
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null
                )
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
    daysCount: Int,
    selectedIndex: Int,
    onSelectedChange: (Int) -> Unit      // 点击回调
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
            isSelected = selectedIndex == 0,
            onClick = {
                onSelectedChange(0)
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
        for (i in 1..daysCount)
        {
            DayNavButton(
                label = i.toString(),
                isSelected = selectedIndex == i,
                onClick = {
                    onSelectedChange(i)
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


@Composable
fun FoldedSheetContent(tripDetailUiState: TripDetailUiState.Success)
{
    Column(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.background(Color(0x00000000)),
            contentAlignment = Alignment.Center
        ) {
            Text("Folded", color = Color.Black)
        }
    }
}

//@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
//@Composable
//fun TripDetailScreenPreview()
//{
//    TripDetailScreen()
//}
