package app.model

data class News(
    val title: String,
    val link: String,
    val imagesPaths: List<String>,
    val body: String
)