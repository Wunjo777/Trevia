package com.example.trevia.ui.record

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.trevia.ui.schedule.TripListViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import com.example.trevia.ui.schedule.TripItemUiState

@Composable
fun TripRecordListScreen(
    viewModel: TripListViewModel = hiltViewModel(),
    onTripClick: (Long) -> Unit
) {
    val tripListUiState by viewModel.tripListUiState.collectAsState()
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(tripListUiState.trips) { trip ->
            TripTimelineItem(
                trip = trip,
                onClick = { onTripClick(trip.tripId) }
            )
        }
    }
}

@Composable
fun TripTimelineItem(
    trip: TripItemUiState,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 16.dp)
    ) {
        // 左侧时间轴部分
        TimeLine(
            tripStartDate = trip.tripStartDate
        )

        Spacer(modifier = Modifier.width(12.dp))

        // 右侧卡片
        TripCard(trip = trip, onClick = onClick)
    }
}


@Composable
fun TimeLine(tripStartDate: String) {
    Column(
        modifier = Modifier.width(60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 顶部圆点
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )

        // 竖线
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(40.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
        )

        // 时间文字（几天后）
        Text(
            text = tripStartDate,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun TripCard(
    trip: TripItemUiState,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = trip.tripName,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = trip.tripLocation,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(
                text = trip.tripDateRange,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
