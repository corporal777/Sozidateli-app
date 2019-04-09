package com.example.ui.search

import com.example.data.models.SearchTypeEvent

data class SearchHolder(
        var text: String? = null,
        var date: Long = 0,
        var dateFrom: Long = 0,
        var dateTo: Long = 0,
        var organizations: ArrayList<SearchTypeEvent> = ArrayList(),
        var categories: ArrayList<SearchTypeEvent> = ArrayList(),
        var totalCountSearchResult:Int? = null
){
    fun clear(){
        text = ""
        date = 0
        dateFrom = 0
        dateTo = 0
        organizations = ArrayList()
        categories = ArrayList()
        totalCountSearchResult = null
    }
}