package com.example.holders.registerEvent

import com.example.data.models.RegisterEventFieldData
import com.xwray.groupie.kotlinandroidextensions.Item

abstract class BaseRegisterItem(
        private val fieldData: RegisterEventFieldData<*>,
        private val onDataChange: (fieldData: RegisterEventFieldData<*>) -> Unit
) : Item(fieldData.field.id.toLong()) {

    protected val field = fieldData.field

    protected fun onDataChange() {
        onDataChange(fieldData)
    }
}