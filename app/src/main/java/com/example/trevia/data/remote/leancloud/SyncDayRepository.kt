package com.example.trevia.data.remote.leancloud

import cn.leancloud.LCObject
import com.example.trevia.data.local.schedule.TripRepository
import com.example.trevia.data.remote.SyncState
import com.example.trevia.data.remote.toLcObjectUpdateIsDelete
import com.example.trevia.di.OfflineRepo
import com.example.trevia.domain.schedule.model.DayModel
import com.example.trevia.domain.schedule.model.TripModel
import com.example.trevia.domain.schedule.model.createNewLcObject
import com.example.trevia.utils.strToIsoLocalDate
import java.time.LocalDate
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncDayRepository @Inject constructor(
    private val service: LeanCloudService,
   @OfflineRepo private val tripRepository: TripRepository
)
{
    //注意：day只能新建和删除，不能修改
    suspend fun upsertDays(tripObjectIdToDayModel: List<Pair<String, DayModel>>): UploadResult
    {
        return service.upsertDatas(tripObjectIdToDayModel.map { (tripObjectId, dayModel) ->
            Pair(
                dayModel.id,
                dayModel.createNewLcObject(tripObjectId)
            )
        })
    }

    suspend fun softDeleteDays(dayModels: List<DayModel>): Map<Long, String>
    {
        val responses = service.softDeleteDatas(dayModels.map {
            toLcObjectUpdateIsDelete(
                "Day",
                it.lcObjectId
            )
        })

        val idMap = mutableMapOf<Long, String>()

        // responses 与 dayModels 一一对应
        //第一次上传的day，获取返回的lcObjectId
        dayModels.indices
            .filter { dayModels[it].lcObjectId == null } // 筛选出 lcObjectId 为 null 的元素
            .forEach { index ->
                val success = responses.getJSONObject(index)
                val lcObjectId = success.getString("objectId")
                idMap[dayModels[index].id] = lcObjectId
            }

        return idMap
    }

    suspend fun getDayModelsAfter(timeStamp: Long): List<DayModel>
    {
        return service
            .getDatasAfter(Date(timeStamp), className = "Day").map {
                it.toDayModel()
            }

    }

    private suspend fun LCObject.toDayModel(): DayModel
    {
        val isDeleted = this.getBoolean("isDeleted")
        return DayModel(
            tripId = if (!isDeleted)
            {
                val tripObjectId = this.getLCObject<LCObject>("trip").objectId
                tripRepository.getTripIdByLcObjectId(tripObjectId)!!
            }
            else -1,
            date = this.getString("date").strToIsoLocalDate(),
            indexInTrip = this.getInt("indexInTrip"),
            lcObjectId = this.objectId,
            syncState = if (isDeleted) SyncState.DELETED else SyncState.SYNCED,
            updatedAt = getDate("updatedAt").time
        )
    }
}