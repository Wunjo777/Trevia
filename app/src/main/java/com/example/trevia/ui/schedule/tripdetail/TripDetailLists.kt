package com.example.trevia.ui.schedule.TripDetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.example.trevia.R
import com.example.trevia.ui.utils.SwipeToDismissItem

@Composable
fun DayList(days: List<DayWithEventsUiState>, onDayClicked: (Long) -> Unit)
{
    LazyColumn(
        modifier = Modifier
    )
    {
        items(items = days, key = { day -> day.dayId })
        { day ->
            DayItem(day, onClick = onDayClicked)
        }
    }
}

@Composable
fun DayItem(
    day: DayWithEventsUiState,
    onClick: (Long) -> Unit
)
{
    val eventText = day.events.joinToString(" -- ") { it.location }
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
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick(day.dayId) }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_small)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = eventText,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null
                )
            }
        }

    }
}


@Composable
fun EventList(
    events: List<EventUiState>,
    onDeleteEvent: (Long) -> Unit,
    onClickEvent: (Long) -> Unit
)
{
    LazyColumn(
        modifier = Modifier
    )
    {
        items(items = events, key = { event -> event.eventId })
        { event ->
            SwipeToDismissItem(
                itemId = event.eventId,
                onDeleteItem = { onDeleteEvent(it) },
                content = { EventItem(event, onClick = onClickEvent) },
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
            )
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
        }
    }
}

