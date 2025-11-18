package com.example.trevia.ui.schedule

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trevia.data.schedule.Trip
import com.example.trevia.ui.theme.TreviaTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import com.example.trevia.R
import androidx.compose.ui.res.painterResource

@Composable
fun TripListScreen(
    tripListViewModel: TripListViewModel = hiltViewModel()
)
{
    val tripListUiState by tripListViewModel.tripListUiState.collectAsState()
    Scaffold()
    { innerPadding ->
        TripListContent(tripListUiState.trips, contentPadding = innerPadding)

    }
}

@Composable
private fun TripListContent(
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
                tripList = tripList,
                contentPadding = contentPadding,
                modifier = Modifier
            )
        }
    }
}

@Composable
private fun TripList(
    tripList: List<TripItemUiState>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues
)
{
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(items = tripList, key = { }) { item ->
            TripItem(
                tripItem = item,
                modifier = Modifier
            )
        }
    }
}

@Composable
private fun TripItem(
    tripItem: TripItemUiState,
    modifier: Modifier = Modifier
)
{
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(R.dimen.padding_small),
                vertical = dimensionResource(R.dimen.padding_medium)
            )
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
        ) {
            Row(
                modifier = Modifier.padding(vertical = dimensionResource(R.dimen.padding_small)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = tripItem.tripName,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize)
                Spacer(Modifier.weight(1f))
                PillBadgeWithIcon(text = "NotImplementedYet")
            }
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier) {
                    Text(text = "NotImplementedYet",
                        fontSize = MaterialTheme.typography.titleMedium.fontSize)
                    Text(text = "NotImplementedYet", fontSize = MaterialTheme.typography.bodyMedium.fontSize)
                    Text(text = "NotImplementedYet", fontSize = MaterialTheme.typography.bodyMedium.fontSize)
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
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF0277BD),
            modifier = Modifier.size(16.dp)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = text,
            color = Color(0xFF0277BD),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun TripItemPreview()
{
    TreviaTheme {
        TripItem(
            tripItem = TripItemUiState(
                tripId = 1,
                tripName = "Trip 1",
                tripLocation = "Paris",
                tripDateRange = "2023-01-01 ~ 2023-01-02"
            )
        )
    }
}
