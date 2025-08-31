package app.service

import it.skrape.core.document
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import it.skrape.selects.*
import it.skrape.selects.html5.*
import java.time.LocalDate

data class MySimpleDataClass(
    val httpStatusCode: Int = 0,
    val httpStatusMessage: String = "",
    val paragraph: String = "",
    var allParagraphs: List<String> = listOf(),
    val allLinks: List<String> = listOf(),
    val allImages: List<String> = listOf()
)

class HtmlExtractionService {

    fun extract(): MutableMap<String, String> {
        val currentDate = LocalDate.now()
        val mapData = mutableMapOf<String, String>()

        val extracted = skrape(HttpFetcher) {
            request {
                url = "https://www.ektu.kz/newsevents.aspx?lang=ru"
                headers = mapOf(
                    "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"
                )
                timeout = 15000
            }

            response {
                MySimpleDataClass(
                    allParagraphs = document.li {
                        findAll {
                            a {
                                eachText.filter { it.startsWith("20") }
                            }
                        }
                    },
                    allLinks = document.a {
                        findAll{
                            filter { it.attribute("href")
                                .startsWith("/newsevents/") }
                        }.eachHref
                    }.map {href-> "https://www.ektu.kz$href"},
                )
            }
        }
        val savedLinks = mutableListOf<String>()
        for (i in 0 until extracted.allLinks.size) {
            val date = LocalDate.parse(extracted.allParagraphs[i].take(10))
            if (date.isAfter(currentDate.minusWeeks(1))) {
                savedLinks.add(extracted.allLinks[i])

            }
        }
        for (i in 0 until savedLinks.size) {
            val newsPage = skrape(HttpFetcher) {
                request {
                    url = extracted.allLinks[i]+"?lang=ru"
                }
                response {
                    MySimpleDataClass(
                        allParagraphs = document.p { findAll { eachText } },
                        paragraph = document.h2 { findFirst { text } },
                        allImages = document.findAll { eachSrc.filter { it.endsWith("jpeg") || it.endsWith("jpg") || it.endsWith("JPG") } }
                    )
                }
            }
            println(newsPage.allImages)
            mapData[newsPage.paragraph] = newsPage.allParagraphs.joinToString("\n\n")
        }
        return mapData
    }
}
