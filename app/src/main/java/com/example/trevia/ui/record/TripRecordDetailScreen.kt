package com.example.trevia.ui.record

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickMultipleVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.trevia.R
import com.example.trevia.ui.imgupload.ImgUploadUiState
import com.example.trevia.ui.imgupload.ImgUploadViewModel
import com.example.trevia.ui.schedule.TripDetail.DayWithEventsUiState
import com.example.trevia.ui.schedule.TripDetail.EventUiState
import com.example.trevia.ui.schedule.TripDetail.TripDetailUiState
import com.example.trevia.ui.schedule.TripDetail.TripDetailViewModel

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun TripRecordDetailScreen(
    navigateBack: () -> Unit,
    tripDetailViewModel: TripDetailViewModel = hiltViewModel(),
    imgUploadViewModel: ImgUploadViewModel = hiltViewModel(),
    maxImgSelection: Int = 2
)
{
    val tripDetailUiState by tripDetailViewModel.tripDetailUiState.collectAsState()
    val imgUploadUiState by imgUploadViewModel.imgUploadUiState.collectAsState()

    //创建用于选择图片的 Launcher
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = PickMultipleVisualMedia(maxImgSelection),
    ) { uris ->
        imgUploadViewModel.onImageSelected(uris)
    }

    Scaffold(
        topBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
            )
            {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.clickable { navigateBack() }
                )
            }
        },
        floatingActionButton = {
            AddPhotoButton(onClick = {
                pickImageLauncher.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
            })
        }) { contentPadding ->
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
                TestImgShower(imgUploadUiState, modifier = Modifier.padding(contentPadding))
//                val days = (tripDetailUiState as TripDetailUiState.Success).days
//                LazyColumn(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(contentPadding)
//                )
//                {
//                    items(items = days, key = { day -> day.dayId })
//                    { day ->
//                        DayItem(day)
//                    }
//                }
            }
        }
    }
}

@Composable
fun TestImgShower(imgUploadUiState: ImgUploadUiState, modifier: Modifier = Modifier)
{
    LazyRow(
        modifier = modifier.fillMaxWidth()
    ) {
        items(imgUploadUiState.thumbnailUris) { uri ->
            Card(
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
            ) {
                AsyncImage(model = uri, contentDescription = null)
            }
        }
    }
}

@Composable
fun DayItem(
    day: DayWithEventsUiState,
)
{
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
        LazyRow(modifier = Modifier.fillMaxWidth())
        {
            items(items = day.events, key = { event -> event.eventId })
            { event ->
                EventItem(event, onClick = {})
            }
        }
    }
}

@Composable
fun EventItem(event: EventUiState, onClick: (Long) -> Unit)
{
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(event.eventId) })
    {
        Column(
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_small))
                .fillMaxWidth()
        ) {
            Text(event.location, style = MaterialTheme.typography.titleMedium)
            Text(event.address, style = MaterialTheme.typography.bodyMedium)
            Text(event.timeRange, style = MaterialTheme.typography.bodyMedium)
            // TODO: 添加一行LazyRow显示缩略图
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
