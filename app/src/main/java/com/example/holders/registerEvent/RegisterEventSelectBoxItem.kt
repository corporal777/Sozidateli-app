package com.example.holders.registerEvent

import com.example.R
import com.example.data.models.RegisterEventFieldData
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import initDropDownView
import kotlinx.android.synthetic.main.item_register_event_selector.*

open class RegisterEventSelectBoxItem(
        private val fieldData: RegisterEventFieldData<String>
) : BaseRegisterItem(fieldData) {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val values = field.values ?: emptyList()
        viewHolder.apply {
            autoCompleteTextView.apply {
                initDropDownView(
                        this,
                        values,
                        fieldData.value,
                        if (field.required) null else resources.getString(R.string.search_filters_not_chosen),
                        { it },
                        { fieldData.value = it }
                )
            }
        }
    }

    override fun getLayout() = R.layout.item_register_event_selector
}