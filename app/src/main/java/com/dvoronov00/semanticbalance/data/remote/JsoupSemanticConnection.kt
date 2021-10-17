package com.dvoronov00.semanticbalance.data.remote

import com.dvoronov00.semanticbalance.data.extension.checkSessionValid
import com.dvoronov00.semanticbalance.data.exception.UserAuthErrorException
import com.dvoronov00.semanticbalance.data.exception.UserSessionExpiredException
import com.dvoronov00.semanticbalance.domain.model.User
import com.dvoronov00.semanticbalance.domain.remote.SemanticConnection
import io.reactivex.rxjava3.core.Observable
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.*

class JsoupSemanticConnection : SemanticConnection {

    private val authUrl = "http://stat.semantic.su/"
    private val accountUrl = "http://stat.semantic.su/user"
    private val servicesReportsUrl = "http://stat.semantic.su/user/service-report"
    private val paymentsReportsUrl = "http://stat.semantic.su/user/payment"
    private val newsUrl = "http://semantic.su/"

    private val userAgent =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.150 Safari/537.36"

    override fun auth(username: String, password: String): Observable<String> {
        return Observable.create<String> {
            val request = Jsoup.connect(authUrl)
                .userAgent(userAgent)
                .method(Connection.Method.POST)
                .data("username", username)
                .data("password", password)
                .execute()

            val error = checkAuthErrors(request.body())
            if (error == null) {
                if (request.hasCookie("PHPSESSID")) {
                    it.onNext(request.cookie("PHPSESSID"))
                } else {
                    it.onError(UserAuthErrorException("Response does not have PHPSESSID"))
                }
            } else {
                it.onError(error)
            }
        }
    }

    override fun getAccountHtml(user: User): Observable<String> {
        return Observable.create {
            user.checkSessionValid()?.let { userThrowable ->
                it.onError(userThrowable)
            }

            val html = Jsoup.connect(accountUrl)
                .userAgent(userAgent)
                .method(Connection.Method.GET)
                .cookie("PHPSESSID", user.session)
                .execute()
                .body()

            val error = checkAuthErrors(html)
            if (error == null) {
                it.onNext(html.toString())
            } else {
                it.onError(error)
            }
        }
    }

    override fun getPaymentsReports(user: User): Observable<String> {
        return Observable.create {
            user.checkSessionValid()?.let { userThrowable ->
                it.onError(userThrowable)
            }

            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT)
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            val endDate = sdf.format(calendar.time)
            calendar.add(Calendar.YEAR, -1)
            val startDate = sdf.format(calendar.time)
            println("startDate: $startDate, endDate: $endDate")
            val html = Jsoup.connect(paymentsReportsUrl)
                .userAgent(userAgent)
                .method(Connection.Method.POST)
                .cookie("PHPSESSID", user.session)
                .data("startDate", startDate)
                .data("endDate", endDate)
                .data("submit", "Показать")
                .execute()
                .body()

            val error = checkAuthErrors(html)
            if (error == null) {
                it.onNext(html.toString())
            } else {
                it.onError(error)
            }
        }
    }

    override fun getServicesReports(user: User): Observable<String> {
        return Observable.create {
            user.checkSessionValid()?.let { userThrowable ->
                it.onError(userThrowable)
            }

            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT)
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            val endDate = sdf.format(calendar.time)
            calendar.add(Calendar.YEAR, -1)
            val startDate = sdf.format(calendar.time)
            println("startDate: $startDate, endDate: $endDate")
            val html = Jsoup.connect(servicesReportsUrl)
                .userAgent(userAgent)
                .method(Connection.Method.POST)
                .cookie("PHPSESSID", user.session)
                .data("startDate", startDate)
                .data("endDate", endDate)
                .data("submit", "Показать")
                .execute()
                .body()

            val error = checkAuthErrors(html)
            if (error == null) {
                it.onNext(html.toString())
            } else {
                it.onError(error)
            }
        }
    }

    private fun checkAuthErrors(body: String): Throwable? {
        val authErrorBlock = Jsoup.parse(body).select("div.alert-block")
        if (authErrorBlock.isNullOrEmpty()) return null

        val userAuthErrorText = authErrorBlock
            .text()
            .trim()
            .trimEnd()

        return if (userAuthErrorText.contains("Вам необходимо авторизоваться")) {
            UserSessionExpiredException()
        } else {
            UserAuthErrorException(userAuthErrorText)
        }
    }


    override fun getNewsHtml(): Observable<String> {
        return Observable.create {
            val html = Jsoup.connect(newsUrl)
                .userAgent(userAgent)
                .method(Connection.Method.GET)
                .execute()
                .body()
            it.onNext(html.toString())
        }
    }

}