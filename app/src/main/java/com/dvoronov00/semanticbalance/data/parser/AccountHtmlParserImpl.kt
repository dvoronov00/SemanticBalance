package com.dvoronov00.semanticbalance.data.parser

import com.dvoronov00.semanticbalance.domain.model.Account
import com.dvoronov00.semanticbalance.domain.model.Service
import com.dvoronov00.semanticbalance.domain.parser.AccountParser
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
 * У провайдера отсутствует API, а разметка на странице без каких либо классов,
 * поэтому парсим как есть "в лоб". Если админ провайдера поменяет разметку всё сломается
 * ¯\_(ツ)_/¯
 */
class AccountHtmlParserImpl(html: String) : AccountParser {

    private val doc: Document = Jsoup.parse(html)

    override fun getAccount(): Account =
        Account(
            getId(),
            getBalance(),
            getTariffName(),
            getSubscriptionFee(),
            getAccountState(),
            getServices()
        )

    private fun getId(): Int {
        return doc
            .select("div.middle")
            .select("div#content")
            .select("table.table")
            .first()
            .select("tr")
            .first()
            .select("td")
            .last()
            .text()
            .toInt()
    }

    private fun getBalance(): String {
        return doc
            .select("div.middle")
            .select("div#content")
            .select("table.table")[2]
            .select("tr")
            .first()
            .select("td")
            .last()
            .select("span.label")
            .text()
            .replace("руб.", "")
            .trim()
    }

    private fun getTariffName(): String {
        return doc
            .select("div.middle")
            .select("div#content")
            .select("table.table")[3]
            .select("tr")[1]
            .select("td")[2]
            .text()
    }

    private fun getSubscriptionFee(): String {
        return doc
            .select("div.middle")
            .select("div#content")
            .select("table.table")[3]
            .select("tr")[1]
            .select("td")[5]
            .text()
    }

    private fun getAccountState(): String {
        return doc
            .select("div.middle")
            .select("div#content")
            .select("table.table")[2]
            .select("tr")[1]
            .select("td")
            .last()
            .select("span.label")
            .text()

    }

    private fun getServices(): List<Service> {
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