package com.hcyacg

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.hcyacg.entity.Branch
import com.hcyacg.github.Commits
import com.hcyacg.github.Issues
import com.hcyacg.github.Pulls
import com.hcyacg.github.Releases
import entity.IssueItem
import entity.PullItem
import entity.Release
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.utils.MiraiLogger
import java.util.*

class GithubTask {
    companion object {
        var switch: Boolean = false
        var logger: MiraiLogger = MiraiLogger.Factory.create(GithubTask::class, "Bot")
        var sha = HashMap<String, String>()
        var num: Int = 0
        var projectJson: JSONObject = JSONObject.parseObject("{}")
        var token: String? = null
        var groups: JSONArray = JSONArray.parseArray("[]")
        var users: JSONArray = JSONArray.parseArray("[]")
        var admin: JSONArray = JSONArray.parseArray("[]")
        var project: JSONArray = JSONArray.parseArray("[]")
        var branches = HashMap<String, List<Branch>>()
        var releases = HashMap<String, Release>()
        var issueItem = HashMap<String, IssueItem>()
        var pullItem = HashMap<String, PullItem>()
        var all: Int = 0
        var taskMillisecond: Long = 5000
    }

    /**
     * 开启推送通知,循环仓库
     */
    private var time = Timer()
    var timerTask: TimerTask? = null

    fun startTask() {
        logger.info("Github推送通知 已开启!")
        try {
            time.purge()
            time.schedule(object : TimerTask() {
                override fun run() {
                    for (e in project) {
                        runBlocking {
                            val list: List<Branch> = branches[e.toString()]!!
                            for (o in list) {
                                Commits().checkCommitUpdate(
                                    projects = e,
                                    branch = o.name,
                                )
                            }

                            Releases().checkReleaseUpdate(
                                projects = e,
                            )
                            Issues().checkIssuesUpdate(
                                projects = e
                            )
                            Pulls().checkPullsUpdate(
                                projects = e
                            )
                        }
                    }

                    if (!switch) {
                        this.cancel()
                        logger.info("Github推送通知 已关闭!")
                    }
                }
            }, Date(), taskMillisecond)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}