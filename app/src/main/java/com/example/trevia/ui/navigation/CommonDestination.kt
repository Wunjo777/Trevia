package com.example.trevia.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.graphics.vector.ImageVector

enum class CommonDestination(
    val route: String,
    val label: String,
    val contentDescription: String,
    val icon: ImageVector
) {
    Schedule("trip_list", "行程规划", "Trip List Screen", Icons.Default.DateRange),
    Recording("trip_record_list", "行程记录", "Trip Record List Screen", Icons.Default.Edit),
}
