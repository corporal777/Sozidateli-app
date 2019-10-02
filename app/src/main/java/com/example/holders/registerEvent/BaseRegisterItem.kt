package com.example.holders.registerEvent

import android.text.InputType
import com.example.R
import com.example.data.models.EventRegisterResponseField
import com.example.data.models.RegisterEventField
import com.example.data.models.RegisterFieldResponse
import com.example.ui.request.RequestPresenter
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.register_event_input.view.*
import java.io.File

abstract class BaseRegisterItem(private val presenter: RequestPresenter) : Item() {

    protected var isFirstBind = true

    fun onDataChange(id: String, data: Any?, subId: String? = null) {
        var field = "field[$id]"
        var forRemove:String? = null
        subId?.let {
            field += "[$subId]"
        }

        if (data is File) {
            field = "file[${id}]"
            forRemove = "field[$id]"
        }

        presenter.onDataChange(field, data,forRemove)
    }
}