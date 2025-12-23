package com.example.trevia.data.remote.leancloud

import android.util.Log
import cn.leancloud.LCFile
import cn.leancloud.LCObject
import cn.leancloud.LCObject.deleteAllInBackground
import cn.leancloud.LCObject.saveAllInBackground
import cn.leancloud.LCQuery
import cn.leancloud.LCUser
import cn.leancloud.json.JSONArray
import com.example.trevia.utils.LeanCloudFailureException
import com.example.trevia.utils.toUtcMillis
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


@Singleton
class LeanCloudService @Inject constructor()
{
    suspend fun register(username: String, password: String, email: String): LCUser =
        suspendCancellableCoroutine { cont ->
            // 创建实例
            val user = LCUser().apply {
                // 等同于 user.put("username", "Tom")
                this.username = username
                this.password = password
                // 可选
                this.email = email
            }
            val disposable = user.signUpInBackground().subscribe(
                { lcUser ->
                    if (cont.isActive) cont.resume(lcUser)
                },
                { error ->
                    if (cont.isActive) cont.resumeWithException(error)
                })
            cont.invokeOnCancellation { disposable.dispose() }
        }

    suspend fun login(username: String, password: String): LCUser =
        suspendCancellableCoroutine { cont ->
            val disposable = LCUser.logIn(username, password).subscribe(
                { lcUser ->
                    if (cont.isActive) cont.resume(lcUser)
                },
                { error ->
                    if (cont.isActive) cont.resumeWithException(error)
                })
            cont.invokeOnCancellation { disposable.dispose() }
        }

    suspend fun upsertDatas(
        pairs: List<Pair<Long, LCObject>>
    ): UploadResult =
        suspendCancellableCoroutine { cont ->

            val lcObjects = pairs.map { it.second }

            val disposable = saveAllInBackground(lcObjects).subscribe(
                { responses ->
                    if (!cont.isActive) return@subscribe

                    val idMap = mutableMapOf<Long, String>()
                    val updatedAtList = mutableListOf<Long>()

                    for (index in 0 until responses.size)
                    {
                        val json = responses.getJSONObject(index)
                        val success = json.getJSONObject("success")
                        val objectId = success.getString("objectId")
                        //获取updatedAt
                        var dateStr = success.getString("updatedAt")
                        if (dateStr == null)
                        {
                            dateStr = success.getString("createdAt")
                        }
                        updatedAtList.add(dateStr.toUtcMillis())
                        if (objectId != null)//插入返回objectId，更新不返回
                        {
                            val localId = pairs[index].first
                            idMap[localId] = objectId
                        }
                    }

                    cont.resume(UploadResult(idMap, updatedAtList))
                },
                { error ->
                    if (cont.isActive) cont.resumeWithException(
                        LeanCloudFailureException(
                            error.message ?: "LeanCloudFailureException",
                            error
                        )
                    )
                }
            )

            cont.invokeOnCancellation { disposable.dispose() }
        }

    suspend fun uploadFile(file: LCFile): LCFile =
        suspendCancellableCoroutine { cont ->
            val disposable = file.saveInBackground().subscribe(
                { lcFile ->
                    if (cont.isActive) cont.resume(lcFile)
                },
                { error ->
                    if (cont.isActive) cont.resumeWithException(
                        LeanCloudFailureException(
                            error.message ?: "LeanCloudFailureException",
                            error
                        )
                    )
                })
            cont.invokeOnCancellation { disposable.dispose() }
        }

    suspend fun softDeleteDatas(lcObjects: List<LCObject>): JSONArray =
        suspendCancellableCoroutine { cont ->
            Log.d("syncup", "get inside suspendCancellableCoroutine")
            val disposable = saveAllInBackground(lcObjects).subscribe(
                { responses ->
                    Log.d("syncup", "softDeleteDatas ON LC success")
                    cont.resume(responses)
                },
                { error ->
                    if (cont.isActive)
                    {
                        Log.e("syncup", "softDeleteDatas ON LC error", error)
                        cont.resumeWithException(
                            LeanCloudFailureException(
                                error.message ?: "LeanCloudFailureException",
                                error
                            )
                        )
                    }
                }
            )
            cont.invokeOnCancellation { disposable.dispose() }
        }

    suspend fun hardDeleteDatasAfter7d(lcObjects: List<LCObject>): Unit =
        suspendCancellableCoroutine { cont ->
            val disposable = deleteAllInBackground(lcObjects).subscribe(
                { responses ->
                    if (!cont.isActive) return@subscribe
                    cont.resume(Unit)
                },
                { error ->
                    // 异常处理
                    if (cont.isActive) cont.resumeWithException(error)
                }
            )
            cont.invokeOnCancellation { disposable.dispose() }
        }

    suspend fun getDatasAfter(lastSyncDate: Date, className: String): List<LCObject> =
        suspendCancellableCoroutine { cont ->
            val allResults = mutableListOf<LCObject>()
            var cursorDate = lastSyncDate

            // 递归分页查询函数
            fun queryPage()
            {
                val query = LCQuery<LCObject>(className).apply {
                    whereGreaterThan("updatedAt", cursorDate)
                    orderByAscending("updatedAt")
                    limit = 100
                }

                val disposable = query.findInBackground().subscribe(
                    { results ->
                        if (results.isNotEmpty())
                        {
                            allResults += results
                            cursorDate = results.last().updatedAt
                            queryPage() // 查询下一页
                        }
                        else
                        {
                            // 查询完成
                            if (cont.isActive) cont.resume(allResults)
                        }
                    },
                    { error ->
                        if (cont.isActive)
                        {
                            cont.resumeWithException(
                                LeanCloudFailureException(
                                    error.message ?: "LeanCloudFailureException",
                                    error
                                )
                            )
                        }
                    }
                )

                // 协程取消时释放资源
                cont.invokeOnCancellation { disposable.dispose() }
            }

            // 开始查询第一页
            queryPage()
        }

    fun logOut()
    {
        LCUser.logOut()
    }

    fun getCurrentUser(): LCUser? = LCUser.getCurrentUser()

}

data class UploadResult(
    val dataIdToLcObjectId: Map<Long, String>,
    val updatedAtList: List<Long>
)
