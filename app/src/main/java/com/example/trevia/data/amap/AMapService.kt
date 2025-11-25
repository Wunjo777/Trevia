package com.example.trevia.data.amap

import android.content.Context
import com.amap.api.services.help.Inputtips
import com.amap.api.services.help.InputtipsQuery
import com.amap.api.services.help.Tip
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AMapService(private val context: Context)
{

    /**
     * 异步获取输入提示
     * @param keyword 用户输入的关键字
     * @param city 限定城市，为空或 null 表示全国
     */
    suspend fun getInputTips(keyword: String, city: String?): List<Tip> =
        suspendCancellableCoroutine { cont ->

            val query = InputtipsQuery(keyword, city)
            val inputTips = Inputtips(context, query)

            cont.invokeOnCancellation {
                inputTips.setInputtipsListener(null) // 防止回调泄露
            }

            inputTips.setInputtipsListener { tipList, rCode ->
                if (cont.isActive) {// 检查协程是否已取消，防止回调泄漏
                    if (rCode == 1000) cont.resume(tipList)
                    else cont.resumeWithException(Exception("高德错误码: $rCode"))
                }
            }

            inputTips.requestInputtipsAsyn()
        }

}