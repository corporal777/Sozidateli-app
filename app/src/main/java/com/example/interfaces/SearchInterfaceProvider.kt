package com.example.interfaces

import com.example.ui.search.SearchInterface

interface SearchInterfaceProvider {
    fun provideSearchInterface(): SearchInterface
}