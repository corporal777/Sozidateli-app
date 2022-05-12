package com.example.data.models

data class EventPassport(
        var series: String? = null,
        var number: String? = null,
        var issuedBy: String? = null,
        var issuedDepartment: String? = null,
        var issuedDate: String? = null
) {
    fun isDataComplete(): Boolean {
        return series?.let { it.length == 4 } ?: false
                && number?.let { it.length == 6 } ?: false
                && !issuedBy.isNullOrEmpty()
                && !issuedDepartment.isNullOrEmpty()
                && !issuedDate.isNullOrEmpty()
    }
}