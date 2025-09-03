package app.bot

import app.repository.LastMessageRepository.load
import app.repository.LastMessageRepository.save
import app.service.HtmlExtractionService
import app.service.isLatestNews
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.TelegramFile
import kotlinx.coroutines.delay
import java.io.File

const val botToken = "8365634958:AAF8qUD0i12UiFhhiYbFkpSMAESLD3pGVfo"

suspend fun startBot() {

    val chatId = ChatId.fromId(1120184201)
    val bot = bot {
        token = botToken
    }

    bot.startPolling()

    while (true) {
        load()
        val news = HtmlExtractionService().extract()
        val newsParams = listOf(
            news.title,
            news.link,
            news.photoPath
        )

        if (isLatestNews(newsParams)) {
            save(newsParams)
                bot.sendPhoto(
                    chatId,
                    TelegramFile.ByFile(File("C:/Shit/images.jpeg")), """<b>${news.title}</b> 
                |
                |<a href="${news.link}">Продолжение на сайте ВКТУ>>></a>""".trimMargin(),
                    ParseMode.HTML,
            )
        }
        delay(5000L)
    }
}