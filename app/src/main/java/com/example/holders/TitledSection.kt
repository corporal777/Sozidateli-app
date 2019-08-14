package com.example.holders

import com.xwray.groupie.Section

class TitledSection(id: Long, title: String? = null) : Section() {

    val titleItem = ListSectionNameItem(id, title)

    init {
        setHeader(titleItem)
        setHideWhenEmpty(true)
    }
}