package com.example.holders.registerEvent

import com.example.R
import com.example.data.models.EventGroup
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import initDropDownView
import kotlinx.android.synthetic.main.item_register_event_selector.*

open class EventRegistrationGroupsItem(
        id: Long,
        private val description: String?,
        private val groups: List<EventGroup>,
        private var selectedGroupId: String?,
        private val onDataChange: (String?) -> Unit
) : Item(id) {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val values = groups
        viewHolder.apply {
            autoCompleteTextView.apply {
                val notSelectedVariant = resources.getString(R.string.search_filters_not_chosen)
                hint = description
                initDropDownView(
                        this,
                        values.map { it.name },
                        groups.find { it.id == selectedGroupId }?.name,
                        notSelectedVariant,
                        { name -> values.find { it.name == name }?.id },
                        {
                            selectedGroupId = it
                            onDataChange(it)
                        }
                )
            }
        }
    }

    override fun getLayout() = R.layout.item_register_event_selector
}