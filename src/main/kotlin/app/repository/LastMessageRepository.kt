package app.repository

import kotlinx.serialization.json.Json
import java.io.File

object LastMessageRepository {

    var lastMessageParams = ""
    private val file = File("src/main/kotlin/app/repository/lastMessage.json")

    fun save(title: String) {
        file.writeText(Json.encodeToString(title))
    }

    fun load() {
        if (file.readText().isBlank()) save("")
        lastMessageParams = Json.decodeFromString<String>(file.readText())
    }
}