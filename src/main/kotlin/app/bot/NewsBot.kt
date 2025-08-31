package app.bot

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.dispatcher.text
import app.service.HtmlExtractionService

fun startBot() {
    val htmlExtractionService = HtmlExtractionService()
    val bot = bot {
        token = "7941041527:AAFEZVFttjv0L-9bfDeH_FZC1d-hA23qhJI"
        dispatch {
            val data = htmlExtractionService.extract()
            data.forEach {
                text {
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        it.key+"\n\n" + it.value
                    )
                }
            }

        }
    }
    bot.startPolling()
}