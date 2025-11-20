package com.example.trevia.ui.schedule

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.trevia.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailScreen(
    modifier: Modifier = Modifier
)
{
    val scaffoldState = rememberBottomSheetScaffoldState()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 128.dp,
        sheetContent = {
            TripDetailSheetContent(scaffoldState)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .background(Color(0xFFB3E5FC))
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Text("Scaffold Content")
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailSheetContent(scaffoldState: BottomSheetScaffoldState, modifier: Modifier = Modifier)
{
    val scope = rememberCoroutineScope()
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
            Column(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.66f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(128.dp), contentAlignment = Alignment.Center
                ) {
                    Text("Swipe up to expand sheet")
                }
                Text("Sheet content")
                Button(
                    modifier = Modifier.padding(bottom = 64.dp),
                    onClick = { scope.launch { scaffoldState.bottomSheetState.partialExpand() } }
                ) {
                    Text("Click to collapse sheet")
                }
            }
        }
        else
        {
            Column(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.66f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = modifier.background(Color(0x00000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Folded", color = Color.Black)
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun TripDetailScreenPreview()
{
    TripDetailScreen()
}
