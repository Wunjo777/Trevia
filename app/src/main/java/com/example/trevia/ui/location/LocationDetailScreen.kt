package com.example.trevia.ui.location

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationDetailScreen(
    vm: LocationDetailViewModel = hiltViewModel(),
    navigateBack: () -> Unit
) {

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("地点详情") },
            navigationIcon = {
                IconButton(onClick = { navigateBack}) {
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
                        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(if (page == 0) Color.Black else Color.DarkGray),
                                contentAlignment = Alignment.Center
                            ) {
                                // 封面/视频占位
                                if (page == 0) {
                                    Icon(
                                        Icons.Filled.PlayArrow,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(56.dp)
                                    )
                                } else {
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
                                        colors = listOf(Color.Transparent, MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
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
                            val tags = listOf("人文", "自然", "打卡")
                            tags.forEach { tag ->
                                AssistChip(
                                    onClick = { /* filter by tag */ },
                                    label = { Text(tag) },
                                    shape = MaterialTheme.shapes.small,
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
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
                                            if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                            shape = CircleShape
                                        )
                                        .padding(4.dp)
                                )
                                if (index < pageCount - 1) Spacer(modifier = Modifier.width(6.dp))
                            }
                        }

                        // 播放按钮快捷操作（右上）
                        IconButton(
                            onClick = { /* 直接播放 */ },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .size(36.dp)
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f), shape = CircleShape)
                        ) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = "播放")
                        }
                    }
                }
            }

            // 2. AI介绍（移除评分/评论，增加小字提示）
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Filled.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
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
                                AssistChip(onClick = { /* */ }, label = { Text(t) }, shape = MaterialTheme.shapes.small)
                            }
                        }
                    }
                }
            }

            // 3. 用户评价（美化每条评价）
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.People, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
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
                        val reviews = listOf(
                            Pair("小张", "很棒的景点！推荐上午去，人不太多。"),
                            Pair("小李", "环境优美，值得一去。带父母很合适。"),
                            Pair("小王", "游客较多，周末注意避开高峰。")
                        )
                        reviews.forEachIndexed { idx, (name, text) ->
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                // 头像占位
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Filled.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(name, fontWeight = FontWeight.Bold)
                                        Spacer(Modifier.width(8.dp))
                                        Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                                        Text("4.5", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                                    }
                                    Spacer(Modifier.height(6.dp))
                                    Text(text, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f))
                                }
                            }
                            if (idx < reviews.size - 1) {
                                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
                            }
                        }
                    }
                }
            }

            // 4. 景点信息（两列布局）
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(8.dp))
                            Text("景点信息", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(8.dp))
                        // 两列信息
                        Column {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                                        Spacer(Modifier.width(8.dp))
                                        Text("地址", fontWeight = FontWeight.SemiBold)
                                    }
                                    Text("深圳市南山区 XXX", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f))
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.Call, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                                        Spacer(Modifier.width(8.dp))
                                        Text("电话", fontWeight = FontWeight.SemiBold)
                                    }
                                    Text("123-456-7890", fontSize = 14.sp)
                                }
                            }
                            Spacer(Modifier.height(10.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                                        Spacer(Modifier.width(8.dp))
                                        Text("开放时间", fontWeight = FontWeight.SemiBold)
                                    }
                                    Text("09:00 - 18:00", fontSize = 14.sp)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.TrendingUp, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                                        Spacer(Modifier.width(8.dp))
                                        Text("人气", fontWeight = FontWeight.SemiBold)
                                    }
                                    Text("高峰期: 周末", fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
            }

            // 5. 当地天气信息（渐变背景）
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(
                                Brush.horizontalGradient(listOf(Color(0xFF8EC5FF), Color(0xFFE0C3FC))),
                                shape = MaterialTheme.shapes.medium
                            )
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxSize()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("当地天气", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Spacer(Modifier.height(8.dp))
                                Text("晴", fontSize = 14.sp, color = Color.White.copy(alpha = 0.9f))
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("25°C", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                Text("18°C", fontSize = 14.sp, color = Color.White.copy(alpha = 0.9f))
                            }
                        }
                    }
                }
            }

            // 6. 官方媒体账号（图标按钮行）
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Public, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(8.dp))
                            Text("官方媒体账号", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            IconButton(onClick = { /* 微信 */ }) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Filled.Message, contentDescription = "微信")
                                    Spacer(Modifier.height(4.dp))
                                    Text("微信", fontSize = 12.sp)
                                }
                            }
                            IconButton(onClick = { /* 微博 */ }) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Filled.Public, contentDescription = "微博")
                                    Spacer(Modifier.height(4.dp))
                                    Text("微博", fontSize = 12.sp)
                                }
                            }
                            IconButton(onClick = { /* 抖音 */ }) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Filled.Videocam, contentDescription = "抖音")
                                    Spacer(Modifier.height(4.dp))
                                    Text("抖音", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun LocationDetailScreenPreview() {
    LocationDetailScreen()
}
