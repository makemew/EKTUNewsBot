package app.bot

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.dispatcher.text
import app.service.HtmlExtractionService
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.inputmedia.InputMedia
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import kotlinx.coroutines.delay
import java.io.File

val botToken = "8365634958:AAF8qUD0i12UiFhhiYbFkpSMAESLD3pGVfo"

fun startBot() {
    val htmlExtractionService = HtmlExtractionService()
    val news = htmlExtractionService.extract()
    val bot = bot {
        token = botToken
        dispatch {
            text {
                val data = htmlExtractionService.extract()
                bot.sendPhoto(
                    chatId = ChatId.fromId(message.chat.id),
                    photo = File("C:/Shit/images.jpeg"),
                    caption = news.title
                )
                bot.sendMessage(
                    chatId = ChatId.fromId(message.chat.id),
                    text = """<a href="${news.link}">Продолжение на сайте ВКТУ</a>""",
                    parseMode= ParseMode.HTML
                )
            }
        }
    }
    bot.startPolling()
}