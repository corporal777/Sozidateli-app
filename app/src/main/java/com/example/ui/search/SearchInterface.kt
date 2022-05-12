package com.example.ui.search

import com.example.data.models.SearchFilter

class SearchInterface {

    var searchText = ""

    var searchTextCallback: (() -> Unit)? = null
    var showFilterCallback: (() -> Unit)? = null

    var initWithFilter: SearchFilter? = null
}