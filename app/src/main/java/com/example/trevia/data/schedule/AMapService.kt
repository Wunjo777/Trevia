package com.example.trevia.data.schedule

import android.content.Context

class AMapService(private val context: Context)
{

    interface InputTipsCallback
    {
        /** 成功返回 Tip 列表 */
        fun onTipsReceived(tips: List<Tip>)

        /** 失败返回错误码 */
        fun onError(rCode: Int)
    }

    /**
     * 异步获取输入提示
     * @param keyword 用户输入的关键字
     * @param city 限定城市，如 "深圳"，为空或 null 表示全国
     * @param callback 回调
     */
    fun getInputTips(keyword: String, city: String? = null, callback: InputTipsCallback)
    {
        if (keyword.isEmpty()) return

        val query = InputtipsQuery(keyword, city)
        query.isCityLimit = true // 限制在当前城市

        val inputTips = Inputtips(context, query)
        inputTips.setInputtipsListener { tipList, rCode ->
            if (rCode == 1000)
            {
                // 成功返回
                callback.onTipsReceived(tipList ?: emptyList())
            }
            else
            {
                callback.onError(rCode)
            }
        }

        inputTips.requestInputtipsAsyn()
    }
}