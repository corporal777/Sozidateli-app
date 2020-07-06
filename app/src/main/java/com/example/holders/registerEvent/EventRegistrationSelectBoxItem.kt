package com.example.holders.registerEvent

import com.example.R
import com.example.data.models.EventRegisterFieldData
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import initDropDownView
import kotlinx.android.synthetic.main.item_register_event_selector.*

open class EventRegistrationSelectBoxItem(
        private val fieldData: EventRegisterFieldData<String>,
        private val editable: Boolean = true,
        onDataChange: (fieldData: EventRegisterFieldData<*>) -> Unit
) : BaseRegisterItem(fieldData, onDataChange) {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val values = field.values ?: emptyList()
        viewHolder.apply {
            autoCompleteTextView.apply {
                isEnabled = editable
                initDropDownView(
                        this,
                        values,
                        fieldData.value,
                        if (field.required) null else resources.getString(R.string.search_filters_not_chosen),
                        { it },
                        {
                            fieldData.value = it
                            onDataChange()
                        }
                )
                hint = fieldData.field.description
            }
        }
    }

    override fun getLayout() = R.layout.item_register_event_selector
}