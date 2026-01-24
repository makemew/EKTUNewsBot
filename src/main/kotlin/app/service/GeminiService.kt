package app.service

import com.google.genai.Client

class GeminiService(private val client: Client) {

    fun getGeminiParaphrase(newsBody: String): String {
        return try {
            println("Request sent")
            val response = client.models.generateContent(
                "gemini-2.5-flash",
                promptBody(newsBody),
                null
            )
            val result = response.text()
            if (result.isNullOrBlank()) {
                throw Exception("Response Gemini body is null")
            }
            println("Response received")
            result
        }  catch(e: Exception) {
            println("Ошибка Gemini: ${e.message}")
            throw e
        }

    }

    private fun promptBody(newsBody: String) = """
        Роль: Ты профессиональный SMM-специалист, пишешь новости вуза от третьего лица.
        
        Задача: Переработай сухой текст новости в привлекательный пост для соцсетей.
        
        Правила:
        1. Придумай цепляющий заголовок с 1 эмодзи.
        2. Используй маркированные списки для ключевых достижений/проектов, используй -.
        3. Сохрани все важные названия (ВКТУ, Bett UK, Саясат Нурбек).
        4. Тон: зависит от контекста новости, если о победах или достижениях энергичный и гордый (положительном), если о смерти преподавателя (негативном), более сдержанный и уважительный.
        5. Используй флаги и тематические иконки, если новость не о чем-то печальном. 
        6. Если речь идет о сроках/дедлайнах чего-либо, в конце поста должна быть отметка Дедлайн: <u>дата дедлайна в формате dd.mm.yyyy</u> 
        7. Нужно уложиться до 724 символов. 
        8. Используй HTML теги чтобы выделять заголовки и важную инфу. Используй ТОЛЬКО разрешенные HTML-теги: <b>, <i>, <s> и ссылки <a>.
        9. Не используй хэштеги. 
        10. Разделяй заголовок, основной текст и список ДВОЙНЫМ переносом строки, чтобы пост не выглядел "кашей". Между пунктами списка делай обычный перенос строки.

        Текст новости: $newsBody
    """.trimIndent()
}

