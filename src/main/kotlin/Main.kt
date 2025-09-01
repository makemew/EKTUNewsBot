import app.bot.startBot
import app.service.HtmlExtractionService

fun main() {
    val htmlExtractionService = HtmlExtractionService()
    htmlExtractionService.extract()
    startBot()
}