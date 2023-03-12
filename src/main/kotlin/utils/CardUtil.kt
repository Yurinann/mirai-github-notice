package com.hcyacg.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import java.util.*

class CardUtil {

    /**
     * 返回卡片
     */
    @Throws(Exception::class)
    suspend fun process(
        message: String,
        html: String,
        avatar: String,
        name: String,
        project: String,
        time: String,
        event: Contact
    ): Message {
        val toExternalResource = ImageUtil.getImage(avatar).toByteArray().toExternalResource()
        val imageId: String = toExternalResource.uploadAsImage(event).imageId
        withContext(Dispatchers.IO) {
            toExternalResource.close()
        }

        return Image(imageId).plus("\n")
            .plus(project).plus("\n\n")
            .plus("标题: $message").plus("\n")
            .plus("时间: $time").plus("\n")
            .plus("提交者: $name").plus("\n\n")
            .plus("提交地址: $html")
    }

    /**
     * 返回卡片
     */
    @Throws(Exception::class)
    suspend fun processCommit(
        message: String,
        html: String,
        avatar: String,
        name: String,
        project: String,
        branch: String,
        time: String,
        event: Contact
    ): Message {
        val toExternalResource = ImageUtil.getImage(avatar).toByteArray().toExternalResource()
        val imageId: String = toExternalResource.uploadAsImage(event).imageId
        withContext(Dispatchers.IO) {
            toExternalResource.close()
        }

        return Image(imageId).plus("\n")
            .plus("当前仓库: $project").plus("\n")
            .plus("当前分支: $branch").plus("\n\n")
            .plus("提交内容: $message").plus("\n")
            .plus("时间: $time").plus("\n")
            .plus("提交者: $name").plus("\n\n")
            .plus("提交地址: $html")
    }

}