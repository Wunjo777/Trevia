package com.example.trevia.ui.user

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.trevia.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MineScreen(
    vm: UserProfileViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
)
{
    val currentUser by vm.currentUser.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        vm.logOutUiState.collect { event ->
            when (event)
            {
                LogOutEvent.Success ->
                {
                    Toast.makeText(
                        context,
                        "退出登录成功",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is LogOutEvent.Error ->
                {
                    Toast.makeText(
                        context,
                        "退出登录失败：${event.msg}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("我的") },
                navigationIcon = {
                    IconButton(
                        onClick = navigateBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.navigate_back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            // ---------- 用户信息 ----------
            UserInfoSection(
                userName = currentUser.username,
                email = currentUser.email
            )

            Spacer(Modifier.height(16.dp))

            // ---------- 账号相关操作 ----------
            ActionSection(
                onSwitchAccount = {
                    vm.logOut()
                },
                onLogout = {
                    vm.logOut()
                }
            )
        }
    }
}

@Composable
fun UserInfoSection(
    userName: String,
    email: String?
)
{
    Card(
        modifier = Modifier
            .padding(dimensionResource(R.dimen.padding_small))
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            Text(text = userName, style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = email ?: "未填写邮箱",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ActionSection(
    onSwitchAccount: () -> Unit,
    onLogout: () -> Unit
)
{
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
    ) {

        SettingItem(
            title = "切换账号",
            onClick = onSwitchAccount
        )

        SettingItem(
            title = "退出登录",
            titleColor = MaterialTheme.colorScheme.error,
            onClick = onLogout
        )
    }
}

@Composable
fun SettingItem(
    title: String,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
)
{
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = title,
            color = titleColor,
            style = MaterialTheme.typography.bodyLarge
        )
    }
    HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
}

//@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
//@Composable
//fun PreviewMineScreen()
//{
//    MineScreen(
//        userName = "张三",
//        email = "zhangsan@example.com",
//        navigateBack = {},
//        onSwitchAccount = {},
//        onLogout = {}
//    )
//}
