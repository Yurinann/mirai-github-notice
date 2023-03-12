package com.hcyacg

import com.hcyacg.GithubTask.Companion.switch
import com.hcyacg.command.Github
import com.hcyacg.github.RateLimits
import com.hcyacg.initial.Configurations.Companion.init
import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.extension.PluginComponentStorage
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.utils.info

object GithubNotice : KotlinPlugin(
    JvmPluginDescription(
        id = "com.hcyacg.github-notice",
        version = "1.6.1",
    ) {
        name("Github更新通知")
        author("Nekoer")
        info(
            """
            Github 更新通知插件。
            """.trimIndent()
        )
    }
) {

    override fun onEnable() {
        CommandManager.registerCommand(Github, true)
        switch = true
        GithubTask().startTask()
        logger.info { "Github更新通知 已加载!" }
    }

    /**
     * 初始化配置文件 以及每次开启时读取配置文件
     */
    override fun PluginComponentStorage.onLoad() {
        init()
    }

}



