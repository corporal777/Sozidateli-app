package com.example.holders.registerEvent

import com.example.data.models.EventRegisterFieldData
import com.xwray.groupie.kotlinandroidextensions.Item

abstract class BaseRegisterItem(
        private val fieldData: EventRegisterFieldData<*>,
        private val onDataChange: (fieldData: EventRegisterFieldData<*>) -> Unit
) : Item(fieldData.field.id.toLong()) {

    protected val field = fieldData.field

    protected fun onDataChange() {
        onDataChange(fieldData)
    }
}