package com.dvoronov00.semanticbalance.data.parser

import com.dvoronov00.semanticbalance.domain.model.News
import com.dvoronov00.semanticbalance.domain.parser.NewsParser
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class NewsParserImpl(html: String) : NewsParser {

    private val doc: Document = Jsoup.parse(html)


    override fun getNews(): List<News> {
        val result = arrayListOf<News>()

        val pTags = doc
            .select("div.cent")
            .select("div.blok")
            .select("p")

        val zagTags = pTags.select("p.zag")
        val textTags = pTags.select("p.text")

        for (i in zagTags.indices) {
            result.add(
                News(
                    title = zagTags[i].select("a").text(),
                    date = textTags[i].select("i").first().text(),
                    textHtml = textTags[i].select("span")[1].html()
                )
            )
        }

        return result
    }
}