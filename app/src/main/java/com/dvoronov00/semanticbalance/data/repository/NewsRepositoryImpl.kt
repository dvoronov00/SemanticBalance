package com.dvoronov00.semanticbalance.data.repository

import com.dvoronov00.semanticbalance.data.parser.NewsParserImpl
import com.dvoronov00.semanticbalance.domain.model.News
import com.dvoronov00.semanticbalance.domain.parser.NewsParser
import com.dvoronov00.semanticbalance.domain.remote.SemanticConnection
import com.dvoronov00.semanticbalance.domain.repository.NewsRepository
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val semanticConnection: SemanticConnection
) : NewsRepository {


    override fun getNews(): Observable<List<News>> {
        return semanticConnection.getNewsHtml()
            .map {
                val parser : NewsParser = NewsParserImpl(it)
                parser.getNews()
            }
    }

}