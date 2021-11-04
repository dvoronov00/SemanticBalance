package com.dvoronov00.semanticbalance.data.parser

import com.dvoronov00.semanticbalance.domain.model.ServiceReport
import com.dvoronov00.semanticbalance.domain.parser.ServicesReportsParser
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
 * У провайдера отсутствует API, а разметка на странице без каких либо классов,
 * поэтому парсим как есть "в лоб". Если админ провайдера поменяет разметку всё сломается
 * ¯\_(ツ)_/¯
 */
class ServicesReportsParserImpl(html: String) : ServicesReportsParser {

    private val doc: Document = Jsoup.parse(html)

    override fun getServicesReports(): List<ServiceReport> {
        val result = arrayListOf<ServiceReport>()
        val html = doc
        println("HTML: $html")
        doc.select("tr")
            .drop(1)
            .dropLast(1)
            .forEach { tr ->
                val td = tr.select("td")
                val date = td[0].text()
                val name = td[1].text()
                val price = td[3].text().split(" руб.").first()
                result.add(ServiceReport(date, name, price))
            }

        return result
    }
}