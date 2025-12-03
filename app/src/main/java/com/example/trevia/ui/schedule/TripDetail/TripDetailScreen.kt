package com.example.trevia.ui.schedule.TripDetail

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.trevia.R
import com.example.trevia.domain.amap.model.TipModel
import com.example.trevia.domain.amap.model.toLocationTipUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailScreen(
    navigateBack: () -> Unit,
    navigateToEditEvent: (Long) -> Unit,
    tripDetailViewModel: TripDetailViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
)
{
    val tripDetailUiState by tripDetailViewModel.tripDetailUiState.collectAsState()
    val keyword by tripDetailViewModel.keyword.collectAsState()
    val tips by tripDetailViewModel.tips.collectAsState()
    val selectedDayId by tripDetailViewModel.selectedDayId.collectAsState()
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
                is TripDetailUiState.Loading  ->
                {
                    Text("NotImplementedYet")
                }

                is TripDetailUiState.NotFound ->
                {
                    Text(stringResource(R.string.trip_not_found))
                }

                is TripDetailUiState.Success  ->
                {
                    TripDetailSheetContent(
                        selectedDayId = selectedDayId,
                        tripDetailUiState = tripDetailUiState as TripDetailUiState.Success,
                        scaffoldState = scaffoldState,
                        onDeleteEvent = tripDetailViewModel::deleteEventById,
                        onClickEvent = navigateToEditEvent,
                        onSelectedDayChange = { tripDetailViewModel.onSelectedDayChange(it) },
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

    if (isAddingEvent && tripDetailUiState is TripDetailUiState.Success)
    {
        LocationSearchContent(
            onAddEventChange = { isAddingEvent = it },
            onKeywordChanged = { keyword ->
                tripDetailViewModel.onKeywordChanged(keyword)
            },
            keyword = keyword,
            onTipClick = tripDetailViewModel::addEventByLocation,
            tips = tips,
            tripId = (tripDetailUiState as TripDetailUiState.Success).tripId,
            dayId = selectedDayId!!
        )
    }
}

@Composable
fun LocationSearchContent(
    tripId: Long,
    dayId: Long,
    onAddEventChange: (Boolean) -> Unit,
    onKeywordChanged: (String) -> Unit,
    keyword: String,
    onTipClick: ( dayId: Long, locationName: String, address: String) -> Unit,
    tips: List<LocationTipUiState>,
)
{
    // 背景变暗
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f))
            .clickable { onAddEventChange(false) }
            .navigationBarsPadding()
    ) {

        // 搜索栏
        SearchLocationBar(
            onClose = { onAddEventChange(false) },
            onKeywordChanged = onKeywordChanged,
            keyword = keyword,
            tips = tips,
            onTipClick = { locationName, address ->
                onTipClick(dayId, locationName, address)
            },
            modifier = Modifier.align(
                Alignment.BottomCenter
            )
        )
    }
}

@Composable
fun SearchLocationBar(
    onClose: () -> Unit,
    onKeywordChanged: (String) -> Unit,
    keyword: String,
    tips: List<LocationTipUiState>,
    onTipClick: (locationName: String, address: String) -> Unit,
    modifier: Modifier = Modifier
)
{
    val focusRequester = remember { FocusRequester() }

    val tmpTipsList: List<LocationTipUiState> = listOf(
        TipModel(
            poiId = "BV10243488",
            name = "市民中心(地铁站)",
            district = "广东省深圳市福田区",
            address = "2号线(8号线);4 号线 / 龙华线"
        ).toLocationTipUiState(),
        TipModel(
            poiId = "B02F300690",
            name = "深圳市民中心",
            district = "广东省深圳市福田区",
            address = "福中三路(市民中心地铁站C口步行190米)"
        ).toLocationTipUiState(),
        TipModel(
            poiId = "B0FFFP097E",
            name = "深圳市民中心C区",
            district = "广东省深圳市福田区",
            address = "福中三路(福田地铁站15号口步行300米)"
        ).toLocationTipUiState(),
        TipModel(
            poiId = "B0KUNCL5Q2",
            name = "市民中心",
            district = "广东省深圳市福田区",
            address = ""
        ).toLocationTipUiState(),
        TipModel(
            poiId = "B02F38SB85",
            name = "深圳市民政局(福中三路)",
            district = "广东省深圳市福田区",
            address = "深南大道深圳市民中心行政服务大厅西37"
        ).toLocationTipUiState(),
        TipModel(
            poiId = "B02F300691",
            name = "深圳市人民政府",
            district = "广东省深圳市福田区",
            address = "莲花街道福中社区福中三路2012号市民中心C区"
        ).toLocationTipUiState(),
        TipModel(
            poiId = "B02F37UFRS",
            name = "深圳市人民政府-外事办公室",
            district = "广东省深圳市福田区",
            address = "深南大道深圳市民中心行政服务大厅西46-50"
        ).toLocationTipUiState(),
        TipModel(
            poiId = "B0LRM7YD6N",
            name = "深圳市民政局",
            district = "广东省深圳市福田区",
            address = "深南大道6009号"
        ).toLocationTipUiState(),
        TipModel(
            poiId = "B02F37UAJ1",
            name = "深圳市人民代表大会常务委员会-信访室",
            district = "广东省深圳市福田区",
            address = "福中三路市政府大楼内(市民中心地铁站B口步行150米)"
        ).toLocationTipUiState(),
        TipModel(
            poiId = "B02F38RWRD",
            name = "深圳市人民政府应急管理办公室",
            district = "广东省深圳市福田区",
            address = "福中三路市民中心C区"
        ).toLocationTipUiState()
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f)
            .background(Color.White, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .padding(16.dp)
    ) {
        LazyColumn(modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))) {
            items(tmpTipsList, key = { it.tipId }) { tip ->
                Column(
                    modifier = Modifier
                        .padding(dimensionResource(R.dimen.padding_small))
                        .clickable {
                            onTipClick(tip.name, tip.address)
                            onClose()
                        }) {
                    Text(
                        text = tip.name,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    Text(
                        text = tip.address,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }


            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
        ) {

            TextField(
                value = keyword,
                onValueChange = { onKeywordChanged(it) },
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailSheetContent(
    selectedDayId: Long?,
    tripDetailUiState: TripDetailUiState.Success,
    scaffoldState: BottomSheetScaffoldState,
    onDeleteEvent: (Long) -> Unit,
    onClickEvent: (Long) -> Unit,
    onSelectedDayChange: (Long?) -> Unit,
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
            ExpandedSheetContent(
                selectedDayId = selectedDayId,
                tripDetailUiState = tripDetailUiState,
                onDeleteEvent = onDeleteEvent,
                onClickEvent = onClickEvent,
                onSelectedDayChange = onSelectedDayChange,
                onAddEventChange = onAddEventChange,
            )
        }
        else
        {
            FoldedSheetContent(tripDetailUiState)
        }
    }
}