package app.bot

import app.model.News
import app.repository.LastMessageRepository.load
import app.repository.LastMessageRepository.save
import app.service.GeminiService
import app.service.HtmlExtractionService
import app.service.isLatestNews
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.TelegramFile
import com.github.kotlintelegrambot.entities.inputmedia.InputMediaPhoto
import com.github.kotlintelegrambot.entities.inputmedia.MediaGroup
import com.google.genai.Client
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.delay

val dotenv = dotenv()
val botToken: String = dotenv["BOT_TOKEN"]
val apiToken: String = dotenv["API_TOKEN"]

suspend fun startBot() {

    val isMyChat = false
    val chatId = if (isMyChat) ChatId.fromId(1120184201) else ChatId.fromId(-1002967159010)

    val bot = bot {
        token = botToken
    }
    val client = Client.builder()
        .apiKey(apiToken)
        .build()
    val geminiService = GeminiService(client)

    try {
        while (true) {
            try {
                load()
                val news = HtmlExtractionService().extract()

                if (isLatestNews(news.title)) {
                    save(news.title)
                    val message = buildMessage(news, geminiService)
                    sendNews(bot, chatId, news, message)
                }
                delay(60_000L)

            } catch (e: Exception) {
                sendExceptionMessage(bot, e, "Main logic error")
                delay(15*60_000L)
            }
        }
    } catch (e: Exception) {
        sendExceptionMessage(bot, e, "Bot crashed")
    }
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
                "Читать на сайте ВКТУ",
                "Узнать больше на сайте ВКТУ"
            ).random()
}

fun sendNews(bot: Bot, chatId: ChatId, news: News, message: String ) {
    when {
        news.imagesPaths.size == 1 -> {
            bot.sendPhoto(
                chatId = chatId,
                photo = TelegramFile.ByFileId(news.imagesPaths[0]),
                caption = message,
                parseMode = ParseMode.HTML
            )
        }

        news.imagesPaths.size > 1 -> {
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

        else -> {
            bot.sendMessage(
                chatId = chatId,
                text = message,
                parseMode = ParseMode.HTML
            )
        }
    }
}

private fun sendExceptionMessage(bot: Bot, e: Exception, title: String) {
    bot.sendMessage(
        chatId = ChatId.fromId(1120184201),
        text = exceptionMessage(e, title),
        parseMode = ParseMode.HTML
    )
}


private fun exceptionMessage(e: Exception, title: String): String {
    val stackSnippet = e.stackTrace
        .take(3)
        .joinToString("\n")
    return """
⚠️ <b>$title</b>: ${e.message}
    
Stack trace:
$stackSnippet
    """.trimIndent()
}

private fun buildMessage(news: News, geminiService: GeminiService): String {
    return buildString {
        if (news.body.isBlank()) {
            append(news.title)
        } else {
            append(geminiService.getGeminiParaphrase(news.body))
        }
        append("\n\n<a href=\"${news.link}\">${randomLink()}</a>")
    }
}
