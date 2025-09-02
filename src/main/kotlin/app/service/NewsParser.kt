package app.service

import app.model.News
import it.skrape.core.document
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import it.skrape.selects.eachHref
import it.skrape.selects.eachSrc
import it.skrape.selects.eachText
import it.skrape.selects.html5.a
import it.skrape.selects.html5.h1
import it.skrape.selects.html5.li
import it.skrape.selects.html5.p
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

    fun extract(): News {

        var extracted = MySimpleDataClass()
        var news = MySimpleDataClass()
        val savedLinks = mutableListOf<String>()

        skrape(HttpFetcher) {
            request {
                url = "https://www.ektu.kz/newsevents.aspx?lang=ru"
                headers = mapOf(
                    "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"
                )
                timeout = 15000
            }

            response {
                extracted = MySimpleDataClass(
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
                    }.map {href-> "https://www.ektu.kz$href"}
                )
            }
        }
        for (i in 0 until 10) {
            LocalDate.parse(extracted.allParagraphs[i].take(10))
            savedLinks.add(extracted.allLinks[i])
        }

        skrape(HttpFetcher) {
            request {
                url = extracted.allLinks.first()+"?lang=ru"
            }
            response {
                news = MySimpleDataClass(
                    allParagraphs = document.p { findAll { eachText } },
                    paragraph = document.h1 { findFirst { text } },
                    allImages = document.findAll { eachSrc.filter { it.endsWith("jpeg") || it.endsWith("jpg") || it.endsWith("JPG") } }
                )
            }
        }
        return News(
            title = news.paragraph,
            link = extracted.allLinks.first()+"?lang=ru",
            photoPath = news.allImages.first()
        )
    }
}


/*val currentDate = LocalDate.now()
        val mapData = mutableMapOf<String, String>()*/

/*val savedLinks = mutableListOf<String>()
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
                       paragraph = document.h1 { findFirst { text } },
                       allImages = document.findAll { eachSrc.filter { it.endsWith("jpeg") || it.endsWith("jpg") || it.endsWith("JPG") } }
                   )
               }
           }
           mapData[newsPage.paragraph] = newsPage.allParagraphs.joinToString("\n\n")
       }*/