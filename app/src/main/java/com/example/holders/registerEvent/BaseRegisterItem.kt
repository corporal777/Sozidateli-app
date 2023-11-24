package com.example.holders.registerEvent

import androidx.databinding.ViewDataBinding
import com.example.data.models.EventRegisterFieldData
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.kotlinandroidextensions.Item

abstract class BaseRegisterItem<T : ViewDataBinding>(
        private val fieldData: EventRegisterFieldData<*>,
        private val onDataChange: (fieldData: EventRegisterFieldData<*>) -> Unit
) : BindableItem<T>(fieldData.field.id.toLong()) {

    protected val field = fieldData.field

    protected fun onDataChange() {
        onDataChange(fieldData)
    }
}