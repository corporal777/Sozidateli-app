package com.example.holders.registerEvent

import com.example.ui.request.RequestPresenter
import com.xwray.groupie.kotlinandroidextensions.Item
import java.io.File

abstract class BaseRegisterItem(private val presenter: RequestPresenter) : Item() {

    protected var isFirstBind = true

    fun onDataChange(id: String, data: Any?, subId: String? = null) {
        var field = "field[$id]"
        var forRemove: String? = null
        subId?.let {
            field += "[$subId]"
        }

        if (data is File) {
            field = "file[${id}]"
            forRemove = "field[$id]"
        }

        presenter.onDataChange(field, data, forRemove)
    }
}