package com.example.ui.search

class SearchInterface {

    var searchText = ""

    var searchTextCallback: (() -> Unit)? = null
    var showFilterCallback: (() -> Unit)? = null
}