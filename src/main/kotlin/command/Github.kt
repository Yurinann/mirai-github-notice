package com.hcyacg.command

import com.hcyacg.GithubNotice
import com.hcyacg.GithubTask
import com.hcyacg.GithubTask.Companion.switch
import com.hcyacg.github.RateLimits
import com.hcyacg.initial.Configurations
import net.mamoe.mirai.console.command.*

object Github : CompositeCommand(GithubNotice, "github") {

    @SubCommand
    @Description("启动监控")
    suspend fun start(context: CommandContext) {
        switch = true
        GithubTask().startTask()
        context.sender.sendMessage("启动成功!")
    }

    @SubCommand
    @Description("停止监控")
    suspend fun stop(context: CommandContext) {
        switch = false
        context.sender.sendMessage("停止成功!")
    }

    @SubCommand
    @Description("重载配置")
    suspend fun reload(context: CommandContext) {
        Configurations.reload()
        context.sender.sendMessage("重载成功!")
    }

    @SubCommand
    @Description("查看速率限制")
    suspend fun rateLimit(context: CommandContext) {
        RateLimits().getRateLimit(context)
    }

}