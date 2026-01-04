package com.example.trevia.ui.record

import android.app.Notification
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickMultipleVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.trevia.R
import com.example.trevia.ui.imgshow.DragMode
import com.example.trevia.ui.imgshow.ImgSelectionEvent
import com.example.trevia.ui.imgshow.ImgSelectionState
import com.example.trevia.ui.imgshow.ImgShowViewModel
import com.example.trevia.ui.imgshow.PhotoUiState
import com.example.trevia.ui.schedule.TripDetail.DayWithEventsUiState
import com.example.trevia.ui.schedule.TripDetail.EventUiState
import com.example.trevia.ui.schedule.TripDetail.TripDetailUiState
import com.example.trevia.ui.schedule.TripDetail.TripDetailViewModel
import com.example.trevia.ui.utils.BottomMenuItem
import com.example.trevia.ui.utils.BottomSheetMenu
import com.example.trevia.ui.utils.FullscreenImageBrowser

data class OpenImagePreview(
    val imgUrls: List<String?>,
    val index: Int
)

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun TripRecordDetailScreen(
    navigateBack: () -> Unit,
    tripDetailViewModel: TripDetailViewModel = hiltViewModel(),
    imgShowViewModel: ImgShowViewModel = hiltViewModel(),
    maxImgSelection: Int = 99
)
{
    val context = LocalContext.current
    val tripDetailUiState by tripDetailViewModel.tripDetailUiState.collectAsState()
    val imgShowUiState by imgShowViewModel.imgShowUiState.collectAsState()
    val imgSelectionState by remember { imgShowViewModel.imgSelectionState }
    var showBottomSheet by remember { mutableStateOf(false) }
    var showEventSelectSheet by remember { mutableStateOf(false) }
    var openImgPreview by remember { mutableStateOf<OpenImagePreview?>(null) }


    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = PickMultipleVisualMedia(maxImgSelection),
    ) { uris ->
        tripDetailViewModel.onImageSelected(uris)
    }

    Scaffold(
        topBar = {
            if (imgSelectionState.enabled)
            {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // 进入选择模式时显示 “叉号”
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Exit Selection",
                        modifier = Modifier
                            .clickable {
                                imgShowViewModel.imgSelectionEventHandler(ImgSelectionEvent.ExitSelection)
                            }
                            .padding(dimensionResource(R.dimen.padding_small))
                    )
                    Text(
                        text = stringResource(
                            R.string.selected_count,
                            imgSelectionState.selectedIds.size
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
                    )
                    Text(
                        text = stringResource(R.string.confirm),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .clickable {
                                showBottomSheet = true
                            }
                            .padding(dimensionResource(R.dimen.padding_small))
                    )
                }
            }
            else
            {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier
                        .clickable { navigateBack() }
                        .padding(dimensionResource(R.dimen.padding_small))
                )
            }
        },
        floatingActionButton = {
            AddPhotoButton {
                pickImageLauncher.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
            }
        }
    ) { contentPadding ->
        when (tripDetailUiState)
        {
            is TripDetailUiState.Loading -> Text("NotImplementedYet")

            is TripDetailUiState.NotFound -> Text(stringResource(R.string.trip_not_found))

            is TripDetailUiState.Success ->
            {
                val days = (tripDetailUiState as TripDetailUiState.Success).days

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    // 固定的未分类图片
                    val unclassifiedPhotos = imgShowUiState.groupedPhotos[-1L]
                    val hasUnclassified = !unclassifiedPhotos.isNullOrEmpty()
                    if (hasUnclassified)
                    {
                        UnclassifiedPhotoContent(
                            context = context,
                            photos = unclassifiedPhotos,
                            imgSelectedIds = imgSelectionState.selectedIds,
                            imgSelectionEnabled = imgSelectionState.enabled,
                            onEvent = imgShowViewModel::imgSelectionEventHandler,
                            onImgClick = { list, idx ->
                                openImgPreview = OpenImagePreview(
                                    imgUrls = list,
                                    index = idx
                                )
                            },
                            reportTTI = tripDetailViewModel::reportTTI,
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                        )
                    }


                    // 滚动的行程天项
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = if (hasUnclassified) 160.dp else 30.dp),
                        contentPadding = contentPadding
                    ) {
                        items(days, key = { it.dayId }) { day ->
                             DayItem(
                                context,
                                day,
                                imgShowUiState.groupedPhotos,
                                imgSelectionState.selectedIds,
                                imgSelectionState.enabled,
                                imgShowViewModel::imgSelectionEventHandler,
                                onImgClick = { list, idx ->
                                    openImgPreview = OpenImagePreview(
                                        imgUrls = list,
                                        index = idx
                                    )
                                }
                            )
                        }
                    }
                }

                openImgPreview?.let { preview ->
                    FullscreenImageBrowser(
                        imageUrls = preview.imgUrls,
                        initialIndex = preview.index,
                        onDismiss = { openImgPreview = null }
                    )
                }

                if (showBottomSheet)
                {
                    BottomSheetMenu(
                        onDismissRequest = { showBottomSheet = false },
                        menuItems = listOf(
                            BottomMenuItem(
                                title = "删除",
                                onClick = {
                                    imgShowViewModel.deleteSelectedPhoto()
                                    imgShowViewModel.imgSelectionEventHandler(ImgSelectionEvent.ExitSelection)
                                }
                            ),
                            BottomMenuItem(
                                title = "移动到",
                                onClick = {
                                    showBottomSheet = false
                                    showEventSelectSheet = true
                                }
                            )
                        )
                    )
                }

                if (showEventSelectSheet)
                {
                    EventSelectBottomSheet(
                        days = days,
                        onDismissRequest = { showEventSelectSheet = false },
                        onEventSelected = { eventId ->
                            imgShowViewModel.moveSelectedPhotosToEvent(eventId)
                            imgShowViewModel.imgSelectionEventHandler(ImgSelectionEvent.ExitSelection)
                            showEventSelectSheet = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventSelectBottomSheet(
    days: List<DayWithEventsUiState>,
    onDismissRequest: () -> Unit,
    onEventSelected: (Long) -> Unit
)
{
    ModalBottomSheet(
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Text(
                text = "选择事件",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            days.forEach { day ->
                day.events.forEach { event ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onEventSelected(event.eventId) }
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = day.date + ": ",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = event.location,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UnclassifiedPhotoContent(
    context: Context,
    photos: List<PhotoUiState>,
    imgSelectedIds: Set<Long>,
    imgSelectionEnabled: Boolean,
    onEvent: (ImgSelectionEvent) -> Unit,
    onImgClick: (List<String?>, Int) -> Unit,
    reportTTI: () -> Unit,
    modifier: Modifier = Modifier
)
{
    //当photos从空变成非空后，等待下一帧渲染完成，并上报TTI
    LaunchedEffect(photos.isNotEmpty()) {
        if (photos.isNotEmpty()) {
            withFrameNanos { }
            reportTTI()
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_small))
    ) {
        Text("未分类", style = MaterialTheme.typography.titleMedium)
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
        ) {
            itemsIndexed(photos)
            { index, photo ->
                val isSelected = imgSelectedIds.contains(photo.photoId)
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .aspectRatio(1f)
                        .padding(dimensionResource(R.dimen.padding_small))
                        .pointerInput(photo.photoId)
                        {
                            detectTapGestures(
                                onLongPress = {
                                    onEvent(
                                        ImgSelectionEvent.EnterSelectionModeAndSelect(photo.photoId)
                                    )
                                },
                                onTap = {
                                    if (imgSelectionEnabled)
                                    {
                                        onEvent(ImgSelectionEvent.Toggle(photo.photoId))
                                    }
                                    else
                                    {
                                        onImgClick(
                                            photos.map { it.largeImgUrl },
                                            index
                                        )
                                    }
                                }
                            )
                        }) {
                    AsyncImage(
                        model = remember(photo) {
                            ImageRequest.Builder(context)
                                .data(photo.thumbnailUrl ?: photo.localOriginUri) // 优先服务端缩略图，回退本地
                                .size(200, 200) // 下采样到 200x200
                                .crossfade(true)
                                .build()
                        },
                        placeholder = painterResource(android.R.drawable.ic_menu_gallery), // 占位图
                        error = painterResource(android.R.drawable.ic_menu_report_image), // 加载失败图
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                    )
                    //图片选中遮罩
                    if (isSelected)
                    {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(Color.White.copy(alpha = 0.5f))
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DayItem(
    context: Context,
    day: DayWithEventsUiState,
    groupedPhotos: Map<Long, List<PhotoUiState>>,
    imgSelectedIds: Set<Long>,
    imgSelectionEnabled: Boolean,
    onEvent: (ImgSelectionEvent) -> Unit,
    onImgClick: (List<String?>, Int) -> Unit,
)
{
    val pagerState = rememberPagerState(pageCount = { day.events.size })
    Column(
        modifier = Modifier
            .padding(dimensionResource(R.dimen.padding_small))
            .fillMaxWidth()
    )
    {
        Text(
            "Day ${day.indexInTrip}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
        )
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
        ) { page ->
            val currentEvent = day.events[page]
             EventItem(
                context,
                currentEvent,
                photos = groupedPhotos[currentEvent.eventId] ?: emptyList(),
                imgSelectedIds,
                imgSelectionEnabled,
                onEvent,
                onImgClick
            )
        }
    }
}

@Composable
fun EventItem(
    context:Context,
    event: EventUiState,
    photos: List<PhotoUiState>,
    imgSelectedIds: Set<Long>,
    imgSelectionEnabled: Boolean,
    onEvent: (ImgSelectionEvent) -> Unit,
    onImgClick: (List<String?>, Int) -> Unit,
)
{
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    )
    {
        Column(
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_small))
                .fillMaxWidth()
        ) {
            Text(event.location, style = MaterialTheme.typography.titleMedium)
            Text(event.address, style = MaterialTheme.typography.bodyMedium)
            Text(event.timeRange, style = MaterialTheme.typography.bodyMedium)

            if (photos.isEmpty())
            {
                Text("添加照片")
            }
            else
            {
                 ThumbnailsGrid(
                    context,
                    photos,
                    imgSelectedIds,
                    imgSelectionEnabled,
                    onEvent,
                    onImgClick
                )
            }
        }
    }
}

@Composable
fun ThumbnailsGrid(
    context:Context,
    photos: List<PhotoUiState>,
    imgSelectedIds: Set<Long>,
    imgSelectionEnabled: Boolean,
    onEvent: (ImgSelectionEvent) -> Unit,
    onImgClick: (List<String?>, Int) -> Unit,
    modifier: Modifier = Modifier
)
{
    LazyVerticalGrid(
        columns = GridCells.Fixed(4), // 每行5张
        modifier = modifier
            .fillMaxWidth()
            .height(600.dp), // 控制最大高度
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        itemsIndexed(photos) { index, photo ->
            val isSelected = imgSelectedIds.contains(photo.photoId)

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .aspectRatio(1f)
                    .padding(dimensionResource(R.dimen.padding_small))
                    .pointerInput(photo.photoId)
                    {
                        detectTapGestures(
                            onLongPress = {
                                onEvent(
                                    ImgSelectionEvent.EnterSelectionModeAndSelect(photo.photoId)
                                )
                            },
                            onTap = {
                                if (imgSelectionEnabled)
                                {
                                    onEvent(ImgSelectionEvent.Toggle(photo.photoId))
                                }
                                else
                                {
                                    onImgClick(photos.map { it.largeImgUrl}, index)
                                }
                            }
                        )
                    }) {
                AsyncImage(
                    model =  remember(photo) {
                        ImageRequest.Builder(context)
                            .data(photo.thumbnailUrl ?: photo.localOriginUri) // 优先服务端缩略图，回退本地
                            .size(200, 200) // 下采样到 200x200
                            .crossfade(true)
                            .build()
                    },
                    placeholder = painterResource(android.R.drawable.ic_menu_gallery), // 占位图
                    error = painterResource(android.R.drawable.ic_menu_report_image), // 加载失败图
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                )
                //图片选中遮罩
                if (isSelected)
                {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(Color.White.copy(alpha = 0.5f))
                    )
                }
            }
        }
    }
}


@Composable
fun AddPhotoButton(onClick: () -> Unit)
{
    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "上传图片"
            )
            Text(
                text = "添加照片",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
