package com.dvoronov00.semanticbalance.domain.parser

import com.dvoronov00.semanticbalance.domain.model.News

interface NewsParser {
    fun getNews() : List<News>
}