package com.example.trevia.data.remote.leancloud

import android.util.Log
import cn.leancloud.LCObject
import cn.leancloud.LCObject.deleteAllInBackground
import cn.leancloud.LCObject.saveAllInBackground
import cn.leancloud.LCUser
import kotlinx.coroutines.suspendCancellableCoroutine
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
    ): Map<Long, String> =
        suspendCancellableCoroutine { cont ->

            val lcObjects = pairs.map { it.second }

            val disposable = saveAllInBackground(lcObjects).subscribe(
                { responses ->
                    if (!cont.isActive) return@subscribe

                    val result = mutableMapOf<Long, String>()

                    for (index in 0 until responses.size)
                    {
                        val json = responses.getJSONObject(index)
                        val success = json.getJSONObject("success")
                        val objectId = success.getString("objectId")
                        if (objectId != null)
                        {
                            val localId = pairs[index].first
                            result[localId] = objectId
                        }
                    }

                    cont.resume(result)
                },
                { error ->
                    if (cont.isActive) cont.resumeWithException(error)
                }
            )

            cont.invokeOnCancellation { disposable.dispose() }
        }


    suspend fun deleteDatas(lcObjects: List<LCObject>): Unit =
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

    suspend fun logOut()
    {
        LCUser.logOut()
    }

    fun getCurrentUser(): LCUser? = LCUser.getCurrentUser()

}
