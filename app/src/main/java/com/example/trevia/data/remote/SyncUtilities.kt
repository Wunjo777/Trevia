package com.example.trevia.data.remote

enum class SyncState {
    PENDING,     // 新建 or 修改过
    SYNCED,      // 和云端一致
    DELETED      // 本地已删除，待同步
}

