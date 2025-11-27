package com.example.trevia.ui.schedule

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.room.Delete
import com.example.trevia.R
import com.example.trevia.ui.utils.DeleteConfirmDialog
import com.example.trevia.ui.utils.SwipeToDismissItem


@Composable
fun TripListScreen(
    navigateToAddTrip: () -> Unit,
    navigateToTripDetail: (Long) -> Unit,
    tripListViewModel: TripListViewModel = hiltViewModel()
)
{
    val tripListUiState by tripListViewModel.tripListUiState.collectAsState()
    Scaffold(floatingActionButton = {
        FloatingActionButton(
            onClick = { navigateToAddTrip() },
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.create_trip_button),
            )
        }
    })
    { innerPadding ->
        TripListContent(
            onDeleteTrip = { tripListViewModel.deleteTripById(it) },
            navigateToTripDetail = navigateToTripDetail,
            tripListUiState.trips,
            contentPadding = innerPadding
        )
    }
}

@Composable
private fun TripListContent(
    onDeleteTrip: (Long) -> Unit,
    navigateToTripDetail: (Long) -> Unit,
    tripList: List<TripItemUiState>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
)
{
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        if (tripList.isEmpty())
        {
            Text(
                text = "NotImplementedYet",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(contentPadding),
            )
        }
        else
        {
            TripList(
                onDeleteTrip = onDeleteTrip,
                navigateToTripDetail = navigateToTripDetail,
                tripList = tripList,
                contentPadding = contentPadding,
                modifier = Modifier
            )
        }
    }
}

@Composable
private fun TripList(
    onDeleteTrip: (Long) -> Unit,
    navigateToTripDetail: (Long) -> Unit,
    tripList: List<TripItemUiState>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues
)
{
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(items = tripList, key = { it.tripId }) { item ->
            SwipeToDismissItem(
                itemId = item.tripId,
                onDeleteItem = { onDeleteTrip(it) },
                content = {
                    TripItem(
                        tripItem = item,
                        navigateToTripDetail = navigateToTripDetail
                    )
                },
                modifier = Modifier
            )
        }
    }
}

@Composable
private fun TripItem(
    tripItem: TripItemUiState,
    navigateToTripDetail: (Long) -> Unit,
    modifier: Modifier = Modifier
)
{
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                dimensionResource(R.dimen.padding_small)
            )
            .clickable {
                navigateToTripDetail(tripItem.tripId)
            }
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
        ) {
            Row(
                modifier = Modifier.padding(vertical = dimensionResource(R.dimen.padding_small)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = tripItem.tripName,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.weight(1f))
                PillBadgeWithIcon(
                    icon = Icons.Default.CheckCircle,
                    text = stringResource(R.string.days_until_trip, tripItem.daysUntilTrip)
                )
            }
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier) {
                    Text(
                        text = tripItem.tripLocation,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = tripItem.tripDateRange + ", " + stringResource(
                            R.string.trip_days_count,
                            tripItem.tripDaysCount,
                            tripItem.tripDaysCount - 1
                        ), style = MaterialTheme.typography.bodyMedium
                    )
                    Text(text = "NotImplementedYet", style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .padding(dimensionResource(R.dimen.padding_small))
                )
                {
                    Image(
                        painter = ColorPainter(Color.Black),
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
fun PillBadgeWithIcon(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier
)
{
    Row(
        modifier = modifier
            .background(
                color = Color(0xFFE3F2FD),
                shape = RoundedCornerShape(50)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF0277BD),
            modifier = Modifier.size(16.dp)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = text,
            color = Color(0xFF0277BD),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

//@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
//@Composable
//private fun TripScreenPreview()
//{
//    TreviaTheme {
//        TripListScreen()
//    }
//}

//@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
//@Composable
//private fun TripItemPreview()
//{
//    TreviaTheme {
//        TripItem(
//            tripItem = TripItemUiState(
//                tripId = 1,
//                tripName = "Trip 1",
//                tripLocation = "Paris",
//                tripDateRange = "2025-11-25 ~ 2025-11-28"
//            )
//        )
//    }
//}
