package app.bot

import app.service.HtmlExtractionService
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.TelegramFile
import java.io.File

val botToken = "8365634958:AAF8qUD0i12UiFhhiYbFkpSMAESLD3pGVfo"

fun startBot() {
    val news = HtmlExtractionService().extract()
    val bot = bot {
        token = botToken
        dispatch {
            text {
                bot.sendPhoto(
                    ChatId.fromId(message.chat.id),
                    TelegramFile.ByFile(File("C:/Shit/images.jpeg")), """<b>${news.title}</b> 
                        |
                        |<a href="${news.link}">Продолжение на сайте ВКТУ>>></a>""".trimMargin(),
                    ParseMode.HTML,
                )
            }
        }
    }
    bot.startPolling()
}