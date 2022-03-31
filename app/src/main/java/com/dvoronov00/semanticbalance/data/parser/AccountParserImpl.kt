package com.dvoronov00.semanticbalance.data.parser

import com.dvoronov00.semanticbalance.domain.model.Service
import com.dvoronov00.semanticbalance.domain.parser.AccountParser
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class AccountParserImpl(html: String) : AccountParser {

    private val doc: Document = Jsoup.parse(html)

    override fun getId(): Int {
        return doc
            .select("div.middle")
            .select("div#content")
            .select("span.label-primary")
            .first()
            .text()
            .replace("Лицевой счет ", "")
            .toInt()
    }

    override fun getBalance(): String {
        return doc
            .select("div.middle")
            .select("div#content")
            .select("table.table")[1]
            .select("tr")
            .first()
            .select("td")
            .last()
            .select("span.label")
            .text()
            .replace("руб.", "")
            .trim()
    }

    override fun getTariffName(): String {
        return doc
            .select("div.middle")
            .select("div#content")
            .select("table.table")[3]
            .select("tr")[1]
            .select("td")[2]
            .text()
    }

    override fun getSubscriptionFee(): String {
        return doc
            .select("div.middle")
            .select("div#content")
            .select("table.table")[3]
            .select("tr")[1]
            .select("td")[5]
            .text()
    }

    override fun getAccountState(): String {
        return doc
            .select("div.middle")
            .select("div#content")
            .select("table.table")[1]
            .select("tr")[1]
            .select("td")
            .last()
            .select("span.label")
            .text()

    }

    override fun getServices(): List<Service> {
        val result = arrayListOf<Service>()
        val servicesTags = doc
            .select("div.middle")
            .select("div#content")
            .select("table.table")[3]
            .select("tr")
            .toList()
            .drop(1) // Удаляем строку с заголовками

        for(tag in servicesTags){
            val td = tag.select("td")
            val serviceName = td[1].text()
            val servicePrice = td[5].text()
            result.add(Service(serviceName, servicePrice))
        }

        return result
    }
}