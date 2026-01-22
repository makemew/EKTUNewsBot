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
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.delay

val dotenv = dotenv()
val botToken: String = dotenv["BOT_TOKEN"]

suspend fun startBot() {

    val isMyChat = false
    val chatId = if (isMyChat) ChatId.fromId(1120184201) else ChatId.fromId(-1002967159010)

    val bot = bot {
        token = botToken
    }

    try {
        bot.startPolling()

        while (true) {
            try {
                load()
                val news = HtmlExtractionService().extract()

                val message = buildString {
                    append("<b>${news.title}</b>\n\n")
                    if (news.previewText.isNotBlank()){
                        append(news.previewText+"\n\n\n")
                    }
                    append("<a href=\"${news.link}\">${randomLink()}</a>")
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
                    } else if (news.imagesPaths.size>1){
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
                    } else {
                        bot.sendMessage(
                            chatId = chatId,
                            text = message,
                            parseMode = ParseMode.HTML
                        )
                    }
                }
                delay(60_000L)

            } catch (e: Exception) {
                bot.sendMessage(
                    chatId = ChatId.fromId(1120184201),
                    text = exceptionMessage(e, "Main logic error"),
                    parseMode = ParseMode.HTML
                )
                delay(15*60_000L)
            }
        }
    } catch (e: Exception) {
        bot.sendMessage(
            chatId = ChatId.fromId(1120184201),
            text = exceptionMessage(e, "Bot crashed"),
            parseMode = ParseMode.HTML
        )
    }
}

fun exceptionMessage(e: Exception, title: String): String {
    val stackSnippet = e.stackTrace
        .take(3)
        .joinToString("\n")
    return """
⚠️ <b>$title</b>: ${e.message}
    
Stack trace:
$stackSnippet
    """.trimIndent()
}

fun randomLink(): String {
    return listOf("📌", "🔗", "➡️", "👉", "🔍").random() + " " +
            listOf(
                "Полная новость на сайте ВКТУ",
                "Подробнее на сайте ВКТУ",
                "Перейти к новости",
                "Читать полностью",
                "Подробности на официальном сайте",
                "Смотреть на сайте ВКТУ",
                "Читать на сайте ВКТУ"
            ).random()
}
