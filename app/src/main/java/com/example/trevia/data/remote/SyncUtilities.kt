package com.example.trevia.data.remote

import cn.leancloud.LCObject
import com.example.trevia.domain.schedule.model.TripModel

enum class SyncState {
    PENDING,     // 新建 or 修改过
    SYNCED,      // 和云端一致
    DELETED      // 本地已删除，待同步
}

fun toLcObjectUpdateIsDelete(className: String, lcObjectId: String?): LCObject
{
    val tripLcObject =LCObject.createWithoutData(className, lcObjectId)
    tripLcObject.put("isDeleted", true)
    return tripLcObject
}