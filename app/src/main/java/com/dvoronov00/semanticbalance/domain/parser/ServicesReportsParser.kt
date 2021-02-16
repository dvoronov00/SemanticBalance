package com.dvoronov00.semanticbalance.domain.parser

import com.dvoronov00.semanticbalance.domain.model.ServiceReport

interface ServicesReportsParser {
    fun getServicesReports() : List<ServiceReport>
}