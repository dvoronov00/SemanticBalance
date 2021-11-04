package com.dvoronov00.semanticbalance.data.parser

import com.dvoronov00.semanticbalance.domain.model.PaymentReport
import com.dvoronov00.semanticbalance.domain.parser.PaymentsReportsParser
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
 * У провайдера отсутствует API, а разметка на странице без каких либо классов,
 * поэтому парсим как есть "в лоб". Если админ провайдера поменяет разметку всё сломается
 * ¯\_(ツ)_/¯
 */
class PaymentsReportsParserImpl(html: String) : PaymentsReportsParser {

    private val doc: Document = Jsoup.parse(html)

    override fun getPaymentsReports(): List<PaymentReport> {
        val result = arrayListOf<PaymentReport>()
        doc.select("tr")
            .drop(1)
            .dropLast(1)
            .forEach { tr ->
                val td = tr.select("td")
                val date = td[2].text()
                val paymentAmount = td[3].text().split(" руб.").first()
                val comment = if (td[4].text().trim().isEmpty()) null else td[4].text()
                val paymentMethod =
                    if (td[5].text().trim().isEmpty()) null else td[5].text()
                result.add(
                    PaymentReport(
                        date = date,
                        paymentAmount = paymentAmount,
                        comment = comment,
                        paymentMethod = paymentMethod
                    )
                )
            }

        return result
    }
}