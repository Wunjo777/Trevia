package com.example.trevia.ui.utils

import android.view.MenuItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable

@Composable
fun BottomSheetMenu(
    show: Boolean,
    onDismiss: () -> Unit,
    menuItems: List<MenuItem>,
    onMenuItemSelected: (MenuItem) -> Unit
) {
    if (show) {
        ModalBottomSheet(
            sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden),
            sheetContent = {
                Column {
                    menuItems.forEach { item ->
                        Text(
                            text = item.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onMenuItemSelected(item)
                                    onDismiss()
                                }
                                .padding(16.dp)
                        )
                    }
                }
            }
        )
    }
}
