package com.example.data.models

data class EventPassport(
        var series: String? = null,
        var number: String? = null,
        var agency: String? = null,
        var code: String? = null,
        var date: String? = null
) {
    fun isDataComplete(): Boolean {
        return series?.let { it.length == 4 } ?: false
                && number?.let { it.length == 6 } ?: false
                && !agency.isNullOrEmpty()
                && !code.isNullOrEmpty()
                && !date.isNullOrEmpty()
    }
}