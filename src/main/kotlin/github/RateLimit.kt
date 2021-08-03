package com.hcyacg.github

import com.alibaba.fastjson.JSONObject
import com.hcyacg.GithubTask.Companion.token
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.utils.MiraiLogger
import okhttp3.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class RateLimit {
    val logger: MiraiLogger = MiraiLogger.create("Bot")
    private val headers =
        Headers.Builder().add("Accept", "application/vnd.github.v3+json").add("Authorization", "token $token")
    private val requestBody: RequestBody? = null
    private val client = OkHttpClient().newBuilder().connectTimeout(60000, TimeUnit.MILLISECONDS)
        .readTimeout(60000, TimeUnit.MILLISECONDS)


    suspend fun getRateLimit(event: GroupMessageEvent) {
        var data: JSONObject? = null
//        var data: String? = null
        var response: Response? = null
        try {

            val request: Request = Request.Builder()
                .url("https://api.github.com/rate_limit")
                .addHeader("Authorization", "token $token")
                .addHeader("Accept", "application/vnd.github.v3+json").build()
            response = client.build().newCall(request).execute()

            if (response.isSuccessful) {
                data = JSONObject.parseObject(response.body?.string())
            }

            val limit = JSONObject.parseObject(data?.getString("rate")).getString("limit")
            val used = JSONObject.parseObject(data?.getString("rate")).getString("used")
            val remaining = JSONObject.parseObject(data?.getString("rate")).getString("remaining")
            val reset = JSONObject.parseObject(data?.getString("rate")).getString("reset")

            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val netDate = Date(reset.toLong() * 1000L)


            event.subject.sendMessage(At(event.sender).plus("\n")
                .plus("Github对您的访问限制:").plus("\n")
                .plus("总次数: $limit").plus("\n")
                .plus("已用: $used").plus("\n")
                .plus("剩余: $remaining").plus("\n")
                .plus("重置时间: ${sdf.format(netDate)}").plus("\n")
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}