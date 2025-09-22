package app.bot

import app.repository.LastMessageRepository.load
import app.repository.LastMessageRepository.save
import app.service.HtmlExtractionService
import app.service.isLatestNews
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.TelegramFile
import com.github.kotlintelegrambot.entities.inputmedia.InputMediaPhoto
import com.github.kotlintelegrambot.entities.inputmedia.MediaGroup
import kotlinx.coroutines.delay

const val botToken = "8365634958:AAF8qUD0i12UiFhhiYbFkpSMAESLD3pGVfo"

suspend fun startBot() {

    val isMyChat = true
    val chatId = if (isMyChat) ChatId.fromId(1120184201) else ChatId.fromId( -1002967159010)

    val bot = bot {
        token = botToken
    }

    bot.startPolling()

    while (true) {
        load()
        val news = HtmlExtractionService().extract()

        val message = buildString {
            append("<b>${news.title}</b>\n\n")
            if (news.previewText.isNotBlank()){
                append(news.previewText+"\n\n")
            }
            append("<a href=\"${news.link}\">Продолжение на сайте ВКТУ>>></a>")
        }

        if (isLatestNews(news.title)) {
            save(news.title)

            if (news.imagesPaths.size == 1) {
                bot.sendPhoto(
                    chatId = chatId,
                    photo = TelegramFile.ByFileId(news.imagesPaths[0]),
                    caption = message,
                    parseMode = ParseMode.HTML
                )
            } else {
                val medias = news.imagesPaths.mapIndexed { index, fileId ->
                    InputMediaPhoto(
                        media = TelegramFile.ByFileId(fileId),
                        caption = if (index == 0) message else null,
                        parseMode = if (index == 0) "HTML" else null
                    )
                }.take(3)
                bot.sendMediaGroup(
                    chatId = chatId,
                    mediaGroup = MediaGroup.from(*medias.toTypedArray())
                )
            }
        }
        delay(5000L)
    }
}