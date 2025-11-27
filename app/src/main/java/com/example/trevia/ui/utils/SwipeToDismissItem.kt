package com.example.trevia.ui.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun <T> SwipeToDismissItem(
    itemId: T,
    onDeleteItem: (T) -> Unit,  // 删除行程的回调
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
)
{
    var showDeleteDialog by remember { mutableStateOf(false) }
    // SwipeToDismissBox 状态，确认滑动方向
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
        initialValue = SwipeToDismissBoxValue.Settled,
        confirmValueChange = { newState ->
            if (newState == SwipeToDismissBoxValue.EndToStart)
            {
                showDeleteDialog = true
            }
            newState != SwipeToDismissBoxValue.EndToStart
        },
    )

    if (showDeleteDialog)
    {
        DeleteConfirmDialog(
            onDeleteConfirm = {
                onDeleteItem(itemId)
                showDeleteDialog = false
            },
            onDeleteCancel = { showDeleteDialog = false }
        )
    }

    SwipeToDismissBox(
        state = swipeToDismissBoxState,
        modifier = modifier.fillMaxSize(),
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            // 处理右侧滑动时显示的删除按钮
            if (swipeToDismissBoxState.dismissDirection == SwipeToDismissBoxValue.EndToStart)
            {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Red)
                        .wrapContentSize(Alignment.CenterEnd)
                        .align(Alignment.CenterVertically)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "",
                        tint = Color.White
                    )
                }
            }
        }
    ) {
            content()
    }
}