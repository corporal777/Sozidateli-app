package com.example.data.models

data class EventPassport(
        var serial: String? = null,
        var num: String? = null,
        var org: String? = null,
        var kod: String? = null,
        var date: String? = null
) {
    fun isDataComplete(): Boolean {
        return serial?.let { it.length == 4 } ?: false
                && num?.let { it.length == 6 } ?: false
                && !org.isNullOrEmpty()
                && !kod.isNullOrEmpty()
                && !date.isNullOrEmpty()
    }
}