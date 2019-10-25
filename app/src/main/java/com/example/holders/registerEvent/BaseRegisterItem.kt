package com.example.holders.registerEvent

import com.example.data.models.RegisterEventFieldData
import com.xwray.groupie.kotlinandroidextensions.Item

abstract class BaseRegisterItem(
        fieldData: RegisterEventFieldData<*>
) : Item(fieldData.field.id.toLong()) {

    protected val field = fieldData.field
}