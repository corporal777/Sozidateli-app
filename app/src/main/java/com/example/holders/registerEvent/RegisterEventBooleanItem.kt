package com.example.holders.registerEvent

import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.EventRegisterFieldData
import com.example.extensions.setRequired
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.item_checkbox.*

open class RegisterEventBooleanItem(
        private val fieldData: EventRegisterFieldData<Boolean>,
        private val editable: Boolean = true,
        onDataChange: (fieldData: EventRegisterFieldData<*>) -> Unit
) : BaseRegisterItem(fieldData, onDataChange) {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            checkbox.apply {
                isEnabled = editable
                text = field.name
                isChecked = fieldData.value ?: false
                setOnCheckedChangeListener { _, isChecked ->
                    fieldData.value = isChecked
                    onDataChange()
                }
            }

            tvDescription.apply {
                val description = field.description
                isVisible = !description.isNullOrEmpty()
                text = description?.setRequired(field.required)
            }
        }
    }

    override fun getLayout() = R.layout.item_register_event_boolean
}