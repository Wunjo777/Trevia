package com.example.trevia.ui.location

import android.os.Build
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
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.trevia.domain.location.model.CommentModel
import com.example.trevia.domain.location.model.PoiDetailModel
import com.example.trevia.domain.location.model.WeatherModel
import com.example.trevia.domain.location.model.ModuleState

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

            // 1. 视频 / 图片轮播（替换原有轮播项）
            item {
                val pageCount = 5
                val pagerState = rememberPagerState(pageCount = { pageCount })
                val scope = rememberCoroutineScope()
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
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize()
                        ) { page ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(if (page == 0) Color.Black else Color.DarkGray),
                                contentAlignment = Alignment.Center
                            ) {
                                // 封面/视频占位
                                if (page == 0)
                                {
                                    Icon(
                                        Icons.Filled.PlayArrow,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(56.dp)
                                    )
                                }
                                else
                                {
                                    Text("图片 ${page + 1}", color = Color.White, fontSize = 20.sp)
                                }
                            }
                        }

                        // 渐变覆盖，增加可读性
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .align(Alignment.BottomStart)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                                        )
                                    )
                                )
                        )

                        // 标签（chips）
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(start = 16.dp, bottom = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val tags = listOf("人文", "自然")
                            tags.forEach { tag ->
                                AssistChip(
                                    onClick = { /* filter by tag */ },
                                    label = { Text(tag) },
                                    shape = MaterialTheme.shapes.small,
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = MaterialTheme.colorScheme.primary.copy(
                                            alpha = 0.12f
                                        ),
                                        leadingIconContentColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                        }

                        // 分页指示器（放大高亮）
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 8.dp)
                        ) {
                            repeat(pageCount) { index ->
                                val selected = pagerState.currentPage == index
                                val size by animateFloatAsState(targetValue = if (selected) 12f else 6f)
                                Box(
                                    modifier = Modifier
                                        .size(Dp(size))
                                        .background(
                                            if (selected) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.onSurface.copy(
                                                alpha = 0.2f
                                            ),
                                            shape = CircleShape
                                        )
                                        .padding(4.dp)
                                )
                                if (index < pageCount - 1) Spacer(modifier = Modifier.width(6.dp))
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

            // 2. AI介绍（移除评分/评论，增加小字提示）
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                Icons.Filled.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("AI介绍", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "这里是AI对该地点的介绍，结合历史、人文和景观点滴，为你呈现高质量的导览介绍与游玩建议。",
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                        )
                        Spacer(Modifier.height(6.dp))
                        // 小字提示：AI 生成，仅供参考
                        Text(
                            "该内容为AI生成，仅供参考",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(Modifier.height(12.dp))
                        // 快捷标签
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val quick = listOf("摄影推荐", "亲子友好", "夜景")
                            quick.forEach { t ->
                                AssistChip(
                                    onClick = { /* */ },
                                    label = { Text(t) },
                                    shape = MaterialTheme.shapes.small
                                )
                            }
                        }
                    }
                }
            }

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

                                is ModuleState.Loading  ->
                                {
                                    Text(
                                        "正在加载评论…",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }

                                is ModuleState.Error    ->
                                {
                                    Text(
                                        "评论加载失败：${commentState.failure.message}",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }

                                is ModuleState.Degraded ->
                                {
                                    Text(
                                        "评论暂不可用（${commentState.reason.name}）",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }

                                is ModuleState.Success  ->
                                {
                                    val comments = commentState.data
                                    if (comments.isEmpty())
                                    {
                                        Text(
                                            "暂无用户评论，快来抢沙发吧～",
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(
                                                alpha = 0.6f
                                            )
                                        )
                                    }
                                    else
                                    {
                                        comments.forEach { comment ->
                                            CommentContent(comment)
                                        }
                                    }
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

                        when (uiState.poiState)
                        {
                            is ModuleState.Loading  ->
                            {
                                Text("正在加载景点信息...", fontSize = 14.sp)
                            }

                            is ModuleState.Error    ->
                            {
                                val error = uiState.poiState as ModuleState.Error
                                Text(
                                    "错误码：${error.failure.code}，错误信息：${error.failure.message}",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }

                            is ModuleState.Degraded ->
                            {
                                val poiStateSnapshot = uiState.poiState as ModuleState.Degraded
                                Text(
                                    "景点信息暂时不可用，原因：${poiStateSnapshot.reason.name}。",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }

                            is ModuleState.Success  ->
                            {
                                val poiStateSnapshot = uiState.poiState as ModuleState.Success
                                PoiDetailContent(poiStateSnapshot.data)
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
                            WeatherPlaceholder(
                                "天气加载失败：${weatherState.failure.message}"
                            )
                        }

                        is ModuleState.Degraded ->
                        {
                            WeatherPlaceholder(
                                "天气暂不可用（${weatherState.reason.name}）"
                            )
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
fun PoiDetailContent(data: PoiDetailModel)
{
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
fun WeatherContent(data: WeatherModel)
{
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
                    "更新时间 ${data.reportTime}",
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
fun CommentContent(comment: CommentModel)
{
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

//@Preview(showBackground = true)
//@Composable
//fun LocationDetailScreenPreview()
//{
//    LocationDetailScreen(navigateBack = {})
//}
