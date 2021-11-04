package com.dvoronov00.semanticbalance.data.remote

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

    override fun auth(username: String, password: String): Observable<String> =
        Observable.create<String> { emitter ->
            val request = Jsoup.connect(authUrl)
                .userAgent(userAgent)
                .method(Connection.Method.POST)
                .data(KEY_USERNAME, username)
                .data(KEY_PASSWORD, password)
                .execute()

            checkAuthErrors(request.body())?.let {
                return@let emitter.onError(it)
            }

            if (request.hasCookie(KEY_PHPSESSID)) emitter.onNext(request.cookie(KEY_PHPSESSID))
            else {
                emitter.onError(UserAuthErrorException("Response does not have $KEY_PHPSESSID"))
            }
        }

    override fun getAccountHtml(user: User): Observable<String> {
        user.checkSessionValid()?.let { userThrowable ->
            return Observable.error(userThrowable)
        }
        return Observable.create<String> { emitter ->
            val html = Jsoup.connect(accountUrl)
                .userAgent(userAgent)
                .method(Connection.Method.GET)
                .cookie(KEY_PHPSESSID, user.sessionId)
                .execute()
                .body()

            checkAuthErrors(html)?.let {
                return@let emitter.onError(it)
            }
            emitter.onNext(html.toString())
        }
    }

    override fun getPaymentsReports(user: User): Observable<String> =
        getReportsByUrl(
            url = paymentsReportsUrl,
            user = user,
        )

    override fun getServicesReports(user: User): Observable<String> =
        getReportsByUrl(
            url = servicesReportsUrl,
            user = user,
        )

    private fun getReportsByUrl(url: String, user: User): Observable<String> {
        user.checkSessionValid()?.let { userThrowable ->
            return Observable.error(userThrowable)
        }
        return Observable.create<String> { emitter ->
            val startEndDate = getStartAndEndDate()
            val html = getReportsHtmlByUrlAndParams(
                url = url,
                sessionId = user.sessionId,
                startDate = startEndDate.first,
                endDate = startEndDate.second,
            )

            checkAuthErrors(html)?.let {
                return@let emitter.onError(it)
            }

            emitter.onNext(html)
        }
    }

    override fun getNewsHtml(): Observable<String> = Observable.create { emitter ->
        emitter.onNext(
            Jsoup.connect(newsUrl)
                .userAgent(userAgent)
                .method(Connection.Method.GET)
                .execute()
                .body()
        )
    }

    private fun getStartAndEndDate(): Pair<String, String> {
        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()

        /** В ЛК почему-то фильтр по дате реализвоан без включения текущей даты, поэтому
         * всегда запрашиваем на завтра */
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val endDate = sdf.format(calendar.time)

        calendar.add(Calendar.YEAR, -1)
        val startDate = sdf.format(calendar.time)

        return Pair(startDate, endDate)
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

    private fun getReportsHtmlByUrlAndParams(
        url: String,
        sessionId: String?,
        startDate: String,
        endDate: String,
    ): String =
        Jsoup.connect(url)
            .userAgent(userAgent)
            .method(Connection.Method.POST)
            .cookie(KEY_PHPSESSID, sessionId)
            .data(KEY_START_DATE, startDate)
            .data(KEY_END_DATE, endDate)
            .data(KEY_SUBMIT, KEY_SUBMIT_VALUE)
            .execute()
            .body()

    companion object {
        private const val userAgent =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.150 Safari/537.36"

        private const val account_url_prefix = "http://stat.semantic.su/"
        private const val authUrl = account_url_prefix
        private const val accountUrl = account_url_prefix + "user"
        private const val servicesReportsUrl = account_url_prefix + "user/service-report"
        private const val paymentsReportsUrl = account_url_prefix + "user/payment"
        private const val newsUrl = "http://semantic.su/"

        private const val KEY_USERNAME = "username"
        private const val KEY_PASSWORD = "password"
        private const val KEY_PHPSESSID = "PHPSESSID"
        private const val KEY_START_DATE = "startDate"
        private const val KEY_END_DATE = "endDate"
        private const val KEY_SUBMIT = "submit"
        private const val KEY_SUBMIT_VALUE = "Показать"
    }
}