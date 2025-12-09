package com.example.trevia.ui.utils

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.trevia.utils.detectTransformGestures
import com.example.trevia.utils.tapAndGesture
import kotlinx.coroutines.launch

/**
 * Fullscreen image viewer composable.
 *
 * @param imageUris list of image URIs/URLs (String). You can pass anything accepted by your image loader.
 * @param initialIndex the initially selected index in the pager.
 * @param onDismiss called when viewer should be closed.
 */
@Composable
fun FullscreenImageBrowser(
    imageUris: List<Uri>,
    initialIndex: Int = 0,
    onDismiss: () -> Unit
)
{
    if (imageUris.isEmpty()) return

    //拦截系统返回键
    BackHandler {
        onDismiss()
    }

    val pagerState = rememberPagerState(initialPage = initialIndex, pageCount = { imageUris.size })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            ZoomableImage(
                image = imageUris[page],
                onSingleTap = { onDismiss() },
                onSwipeDownToDismiss = { /* optional: you can call onDismiss() if threshold exceeded */ },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Top bar: index + close
        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 28.dp)
                .wrapContentWidth(),
            color = Color.Transparent
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                val indexText = "${pagerState.currentPage + 1} / ${imageUris.size}"
                Text(
                    text = indexText,
                    fontSize = 16.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .background(Color(0x33000000), shape = CircleShape)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        // Close button (top-right)
        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0x22000000))
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.White
            )
        }
    }
}

/**
 * ZoomableImage: supports pinch-to-zoom, pan and double-tap toggling zoom.
 *
 * @param image string accepted by Coil AsyncImage (url, file:, content:, etc.)
 * @param onSingleTap called when user single taps (we use it to dismiss in example)
 */
@Composable
fun ZoomableImage(
    image: Uri,
    modifier: Modifier = Modifier,
    onSingleTap: () -> Unit = {},
    onSwipeDownToDismiss: (() -> Unit)? = null
)
{
    // scale and translation state
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    // Limits
    val minScale = 1f
    val maxScale = 5f

    Box(
        modifier = modifier.tapAndGesture(onTap = { onSingleTap() }, onDoubleTap = {
            // toggle between 1f and 2.75f (or maxScale)
            val target = if (scale > 1.5f) 1f else 2.75f.coerceAtMost(maxScale)
            scale = target
            if (target == 1f) offset = Offset.Zero
        }, onGesture = { centroid, pan, zoom, gestureRotation ->
            // apply zoom around centroid roughly
            val newScale = (scale * zoom).coerceIn(minScale, maxScale)
            scale = newScale
            // apply pan scaled by current scale (simple)
            if (newScale > 1.0f) offset += pan
        }, scrollEnabled = remember { mutableStateOf(true) })
    ) {
        // Use Coil's AsyncImage; swap to your loader if needed.
        AsyncImage(
            model = image,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                // apply scale and translation (convert offset to IntOffset)
                .graphicsLayer {
                    // translation uses pixels; offset is in pixels already from gestures
                    translationX = offset.x
                    translationY = offset.y
                    scaleX = scale
                    scaleY = scale
                }
        )
    }

    // When fully unzoomed restore offset (small debounce)
    LaunchedEffect(scale) {
        if (scale <= 1.01f)
        {
            offset = Offset.Zero
        }
    }
}


