package app.repository

import kotlinx.serialization.json.Json
import java.io.File

object LastMessageRepository {

    var lastMessageParams = mutableListOf<String>()
    private val file = File("src/main/kotlin/app/repository/lastMessage.json")

    fun save(list: List<String>) {
        file.writeText(Json.encodeToString(list))
    }

    fun load() {
        if (file.readText().isBlank()) save(listOf())
        lastMessageParams = Json.decodeFromString<List<String>>(file.readText()).toMutableList()
    }
}