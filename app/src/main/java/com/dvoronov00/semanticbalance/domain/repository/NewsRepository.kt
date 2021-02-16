package com.dvoronov00.semanticbalance.domain.repository

import com.dvoronov00.semanticbalance.domain.model.News
import io.reactivex.rxjava3.core.Observable

interface NewsRepository {

    fun getNews() : Observable<List<News>>

}