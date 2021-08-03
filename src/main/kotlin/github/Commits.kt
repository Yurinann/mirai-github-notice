package com.hcyacg.github

import com.alibaba.fastjson.JSONObject
import com.hcyacg.GithubNotice
import com.hcyacg.GithubTask
import com.hcyacg.GithubTask.Companion.groups
import com.hcyacg.GithubTask.Companion.logger
import com.hcyacg.GithubTask.Companion.num
import com.hcyacg.GithubTask.Companion.project
import com.hcyacg.GithubTask.Companion.sha
import com.hcyacg.GithubTask.Companion.token
import com.hcyacg.GithubTask.Companion.users


import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.RichMessage
import net.mamoe.mirai.utils.MiraiExperimentalApi
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.closeQuietly
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class Commits {

    private val client = OkHttpClient().newBuilder().connectTimeout(60000, TimeUnit.MILLISECONDS)
        .readTimeout(60000, TimeUnit.MILLISECONDS)

    /**
     * 检查github推送更新
     */
    suspend fun checkUpdate(
        event: CommandSender,
        projects: Any?,
        branch: Any?,
        index: Int
    ) {
        var name: Any? = null
        var time: Any? = null
        var html: Any? = null
        var avatar: Any? = null
        var message: Any? = null
        var stA: String? = null
        var sha1: Any? = null
        var response: Response? = null
//        logger.warning("${projects.toString()} => ${branch.toString()}")


        try {
            val request: Request = Request.Builder()
                .url("https://api.github.com/repos/${projects.toString()}/commits/${branch.toString()}")
                .addHeader("Authorization", "token $token")
                .addHeader("Accept", "application/vnd.github.v3+json").build()
            response = client.build().newCall(request).execute()

            if (response.isSuccessful) {
                stA = response.body?.string()
            }

            val jsonObject: JSONObject? = JSONObject.parseObject(stA)

            if (null != jsonObject) {
                val sha1: Any? = jsonObject["sha"]
                if (sha[projects].contentEquals(sha1.toString())) {
                    return
                }
                sha[projects.toString()] = sha1.toString()

                val commit: Any? = jsonObject["commit"]
                val committer: Any? = JSONObject.parseObject(commit.toString())["committer"]
                name = JSONObject.parseObject(committer.toString())["name"]
                val date: Any? = JSONObject.parseObject(committer.toString())["date"]

                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US)
                val myDate: Date = dateFormat.parse(date.toString().replace("Z", "+0000"))
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                time = sdf.format(Date(java.lang.String.valueOf(myDate.time).toLong()))

                message = JSONObject.parseObject(commit.toString())["message"]
                html = jsonObject["html_url"]

                val committers: Any? = jsonObject["committer"]
                avatar = JSONObject.parseObject(committers.toString())["avatar_url"]

                if (num >= project.size) {

                    for (e in groups) {

                        //BUG注 需判断该机器人群组是否存在该群
                        if (null != event.bot?.getGroup(e.toString().toLong())) {
                            event.bot?.getGroup(e.toString().toLong())?.sendMessage(
                                process(
                                    message = message.toString(),
                                    html = html.toString(),
                                    avatar = avatar.toString(),
                                    time = time.toString(),
                                    name = name.toString()
                                )
                            )

                        }

//                        event.bot.getGroup(e.toString().toLong())?.sendMessage("${name}推送了代码\n${message}\n${time}\n${html}")
                    }

                    for (u in users) {

                        if (null != event.bot?.getStranger(u.toString().toLong())) {
                            event.bot?.getStranger(u.toString().toLong())?.sendMessage(
                                process(
                                    message = message.toString(),
                                    html = html.toString(),
                                    avatar = avatar.toString(),
                                    time = time.toString(),
                                    name = name.toString()
                                )
                            )
                        }

                    }
                } else {
                    num += 1
                }
            }
            response.closeQuietly()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 返回卡片
     */
    @OptIn(MiraiExperimentalApi::class)
    @Throws(Exception::class)
    fun process(
        message: String,
        html: String,
        avatar: String,
        name: String,
        time: String,
    ): Message {
        val contact: String =
            "{\"app\":\"com.tencent.structmsg\",\"desc\":\"新闻\",\"view\":\"news\",\"ver\":\"0.0.0.1\",\"prompt\":\"Github更新通知\",\"appID\":\"\",\"sourceName\":\"\",\"actionData\":\"\",\"actionData_A\":\"\",\"sourceUrl\":\"\",\"meta\":{\"news\":{\"app_type\":1,\"appid\":\"100951776\",\"desc\":\"${message}\",\"jumpUrl\":\"${html}\",\"preview\":\"${avatar}\",\"tag\":\"哔哩哔哩\",\"title\":\"${name}推送了\"}},\"config\":{\"autosize\":true,\"ctime\":${Date().time},\"forward\":true,\"type\":\"normal\"},\"text\":\"\",\"extraApps\":[],\"sourceAd\":\"\",\"extra\":\"{\\\"app_type\\\":1,\\\"appid\\\":100951776,\\\"uin\\\":895018766}\"}"
        return RichMessage.share(url = html, title = name + "推送了代码", content = message + "\n${time}", coverUrl = avatar)
//        return LightApp(contact)
    }

}