package com.example.trevia.ui.location

import android.net.Uri
import android.os.Build
import coil3.compose.AsyncImage
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickMultipleVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.trevia.domain.location.model.CommentModel
import com.example.trevia.domain.location.model.CommentsDecision
import com.example.trevia.domain.location.model.FailureReason
import com.example.trevia.domain.location.model.MediaDecision
import com.example.trevia.domain.location.model.ModuleState
import com.example.trevia.domain.location.model.PoiDecision
import com.example.trevia.domain.location.model.PoiDetailModel
import com.example.trevia.domain.location.model.VideoQuality
import com.example.trevia.domain.location.model.WeatherDecision
import com.example.trevia.domain.location.model.WeatherModel
import com.example.trevia.ui.location.MediaContent
import com.example.trevia.ui.utils.VideoPlayerWithLifecycle
import androidx.core.net.toUri
import com.example.trevia.utils.formatMillis

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationDetailScreen(
    vm: LocationDetailViewModel = hiltViewModel(),
    maxImgSelection: Int = 99,
    navigateBack: () -> Unit
)
{
    val uiState by vm.uiState.collectAsState()
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = PickMultipleVisualMedia(maxImgSelection),
    ) { uris ->
        vm.onImageSelected(uris)
    }

    var showCommentDialog by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("地点详情") },
            navigationIcon = {
                IconButton(onClick = { navigateBack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                }
            },
            actions = {
                IconButton(onClick = { /* 收藏 */ }) {
                    Icon(Icons.Filled.FavoriteBorder, contentDescription = "收藏")
                }
                IconButton(onClick = { /* 分享 */ }) {
                    Icon(Icons.Filled.Share, contentDescription = "分享")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface
            )
        )
    }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // 1. 多媒体内容
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .padding(horizontal = 16.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(10.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        when(val mediaState = uiState.mediaState)
                        {
                            is ModuleState.Loading -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                            is ModuleState.Success -> {
                                MediaContent(mediaState.data)
                            }
                            is ModuleState.Empty -> {
                                Text(
                                    text = "暂无视频/图片信息",
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                            is ModuleState.Error -> {
                                if (mediaState.failure.reason == FailureReason.NO_NETWORK)
                                {
                                    Text(
                                        "暂无网络连接，请检查后重试",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                                else
                                {
                                    val message =
                                        if (mediaState.failure.canRetry)
                                            "多媒体加载失败，请重试"
                                        else
                                            "多媒体加载失败：${mediaState.failure.message}"
                                    Text(
                                        message,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                        // 右下角「点击添加图片」按钮
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 16.dp, bottom = 16.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.55f)
                                )
                                .clickable {
                                    pickImageLauncher.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.AddPhotoAlternate,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "点击添加图片",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            //region 2. AI介绍（移除评分/评论，增加小字提示）
//            item {
//                Card(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 16.dp),
//                    shape = MaterialTheme.shapes.medium,
//                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
//                    elevation = CardDefaults.cardElevation(6.dp)
//                ) {
//                    Column(modifier = Modifier.padding(16.dp)) {
//                        Row(
//                            verticalAlignment = Alignment.CenterVertically,
//                            modifier = Modifier.fillMaxWidth()
//                        ) {
//                            Icon(
//                                Icons.Filled.Info,
//                                contentDescription = null,
//                                tint = MaterialTheme.colorScheme.primary
//                            )
//                            Spacer(Modifier.width(8.dp))
//                            Text("AI介绍", fontSize = 18.sp, fontWeight = FontWeight.Bold)
//                        }
//                        Spacer(Modifier.height(8.dp))
//                        Text(
//                            "这里是AI对该地点的介绍，结合历史、人文和景观点滴，为你呈现高质量的导览介绍与游玩建议。",
//                            fontSize = 15.sp,
//                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
//                        )
//                        Spacer(Modifier.height(6.dp))
//                        // 小字提示：AI 生成，仅供参考
//                        Text(
//                            "该内容为AI生成，仅供参考",
//                            fontSize = 12.sp,
//                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
//                        )
//                        Spacer(Modifier.height(12.dp))
//                        // 快捷标签
//                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//                            val quick = listOf("摄影推荐", "亲子友好", "夜景")
//                            quick.forEach { t ->
//                                AssistChip(
//                                    onClick = { /* */ },
//                                    label = { Text(t) },
//                                    shape = MaterialTheme.shapes.small
//                                )
//                            }
//                        }
//                    }
//                }
//            }
            //endregion

            // 3. 用户评价（美化每条评价）
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {

                    // ===== 原有 Card，不动结构 =====
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {

                            // ===== 原有标题行 =====
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Outlined.People,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text("用户评价", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                }
                                Text(
                                    "查看所有 >",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.clickable { }
                                )
                            }

                            Spacer(Modifier.height(8.dp))

                            when (val commentState = uiState.commentState)
                            {
                                is ModuleState.Loading ->
                                {
                                    Text(
                                        "正在加载评论…",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }

                                is ModuleState.Error   ->
                                {
                                    if (commentState.failure.reason == FailureReason.NO_NETWORK)
                                    {
                                        Text(
                                            "暂无网络连接，请检查后重试",
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                    else
                                    {
                                        val message =
                                            if (commentState.failure.canRetry)
                                                "评论加载失败，请重试"
                                            else
                                                "评论加载失败：${commentState.failure.message}"
                                        Text(
                                            message,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }

                                is ModuleState.Empty   ->
                                {
                                    Text(
                                        "暂无用户评论，快来抢沙发吧～",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(
                                            alpha = 0.6f
                                        )
                                    )
                                }

                                is ModuleState.Success ->
                                {
                                    val commentDecision = commentState.data
                                    CommentsContent(commentDecision)
                                }
                            }

                        }
                    }

                    // ===== 右下角「添加评论」按钮 =====
                    FloatingActionButton(
                        onClick = { showCommentDialog = true },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(24.dp)
                    ) {
                        Icon(Icons.Filled.AddComment, contentDescription = "添加评论")
                    }
                }
            }

            // 4. 景点信息（两列布局）
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Outlined.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("景点信息", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(8.dp))

                        when (val poiState=uiState.poiState)
                        {
                            is ModuleState.Loading  ->
                            {
                                Text("正在加载景点信息...", fontSize = 14.sp)
                            }

                            is ModuleState.Error    ->
                            {
                                if (poiState.failure.reason == FailureReason.NO_NETWORK)
                                {
                                    Text(
                                        "暂无网络连接，请检查后重试",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                                else
                                {
                                    val message =
                                        if (poiState.failure.canRetry)
                                            "地点信息加载失败，请重试"
                                        else
                                            "地点信息加载失败：${poiState.failure.message}"
                                    Text(
                                        message,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }

                            is ModuleState.Empty ->
                            {
                                Text(
                                    "暂无景点信息",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }

                            is ModuleState.Success  ->
                            {
                                PoiDetailContent(poiState.data)
                            }
                        }
                    }
                }
            }

            // 5. 当地天气信息（基于 ModuleState）
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    when (val weatherState = uiState.weatherState)
                    {

                        is ModuleState.Loading  ->
                        {
                            WeatherPlaceholder("正在加载天气…")
                        }

                        is ModuleState.Error    ->
                        {
                            if (weatherState.failure.reason == FailureReason.NO_NETWORK)
                            {
                                Text(
                                    "暂无网络连接，请检查后重试",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            else
                            {
                                val message =
                                    if (weatherState.failure.canRetry)
                                        "天气加载失败，请重试"
                                    else
                                        "天气加载失败：${weatherState.failure.message}"
                                Text(
                                    message,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        is ModuleState.Empty ->
                        {
                            WeatherPlaceholder("暂无天气信息")
                        }

                        is ModuleState.Success  ->
                        {
                            WeatherContent(weatherState.data)
                        }
                    }
                }
            }
        }
    }
    if (showCommentDialog)
    {
        AlertDialog(
            onDismissRequest = { showCommentDialog = false },
            title = {
                Text("添加评论")
            },
            text = {
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    placeholder = { Text("请输入你的评论…") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (commentText.isNotBlank())
                        {
                            vm.onCommentUpload(commentText)
                            commentText = ""
                            showCommentDialog = false
                        }
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showCommentDialog = false
                    }
                ) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
fun PoiDetailContent(poiDecision: PoiDecision)
{
    if (!poiDecision.showPoiInfo) return

    val data = poiDecision.data
    // 两列信息
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("地址", fontWeight = FontWeight.SemiBold)
                }
                Text(
                    data.address,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Call,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("电话", fontWeight = FontWeight.SemiBold)
                }
                Text(data.tel, fontSize = 14.sp)
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("邮箱/邮编", fontWeight = FontWeight.SemiBold)
                }
                Text(data.email + " " + data.postCode, fontSize = 14.sp)
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.TrendingUp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("网址", fontWeight = FontWeight.SemiBold)
                }
                Text(data.website, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun WeatherContent(weatherDecision: WeatherDecision)
{
    if (!weatherDecision.showWeather) return

    val data = weatherDecision.data
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF8EC5FF),
                        Color(0xFFE0C3FC)
                    )
                ),
                shape = MaterialTheme.shapes.medium
            )
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "当地天气",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "${data.weather} · 湿度 ${data.humidity}%",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Text(
                    "${data.windDirection}风 ${data.windPower}",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${data.temperature}°C",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    "更新时间 ${formatMillis(data.updatedAt)}",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun WeatherPlaceholder(text: String)
{
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(
                Color.LightGray.copy(alpha = 0.25f),
                shape = MaterialTheme.shapes.medium
            )
            .padding(16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun CommentsContent(commentDecision: CommentsDecision)
{
    if (!commentDecision.showComments) return

    commentDecision.data.forEach { comment ->
    // ===== 单条评论 UI =====
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(
                            alpha = 0.2f
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    "游客评价",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    comment.content,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.9f
                    )
                )
            }
        }
    }
}

@Composable
fun MediaContent(mediaDecision: MediaDecision) {

    // 1️⃣ 决策层已经保证一致性，UI 只信 decision
    val videosAvailable = mediaDecision.showVideo
    val imagesAvailable = mediaDecision.showImage

    if (!videosAvailable && !imagesAvailable) return

    val media = mediaDecision.data

    // 2️⃣ 根据 videoQuality 选择视频 URL
    val videoUrl = remember(mediaDecision) {
        when (mediaDecision.videoQuality) {
            VideoQuality.SMALL -> media.videoUrlSmall
            VideoQuality.MEDIUM -> media.videoUrlMedium
            VideoQuality.LARGE -> media.videoUrlLarge
            else -> null
        }
    }

    // 3️⃣ 总页数：视频 1 页 + 图片 N 页
    val pageCount =
        (if (videosAvailable) 1 else 0) +
                (if (imagesAvailable) media.imgUrls.size else 0)

    val pagerState = rememberPagerState { pageCount }

    Box(modifier = Modifier.fillMaxSize()) {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->

            when {
                // 4️⃣ 视频页永远是第 0 页
                videosAvailable && page == 0 -> {
                    VideoPlayerWithLifecycle(
                        videoUri = videoUrl!!.toUri(),
                        autoPlay = mediaDecision.autoPlayVideo
                    )
                }

                // 5️⃣ 图片页
                imagesAvailable -> {
                    val imageIndex = if (videosAvailable) page - 1 else page
                    AsyncImage(
                        model = media.imgUrls[imageIndex],
                        contentDescription = "Image $imageIndex",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        // 6️⃣ 分页指示器
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(8.dp)
        ) {
            repeat(pageCount) { index ->
                val selected = pagerState.currentPage == index
                val size by animateFloatAsState(
                    targetValue = if (selected) 12f else 6f,
                    label = "pager_indicator"
                )

                Box(
                    modifier = Modifier
                        .size(size.dp)
                        .background(
                            if (selected)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                            shape = CircleShape
                        )
                )

                if (index < pageCount - 1) {
                    Spacer(modifier = Modifier.width(6.dp))
                }
            }
        }
    }
}
