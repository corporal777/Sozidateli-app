package com.example.holders.redesign

import androidx.viewbinding.ViewBinding
import com.example.app.databinding.ItemEventDetailMainBinding
import com.example.data.models.EventNew
import com.xwray.groupie.viewbinding.BindableItem

abstract class CustomBindingItem<T : ViewBinding> : BindableItem<T>{

    constructor() : super()
    constructor(id : Long) : super(id)

    override fun bind(viewBinding: T, position: Int, payloads: MutableList<Any>) {
        val payload = payloads.firstOrNull()
        if (payload == null) super.bind(viewBinding, position, payloads)
        else bind(viewBinding, payload)
    }

    open fun bind(binding: T, payload: Any) { }
}