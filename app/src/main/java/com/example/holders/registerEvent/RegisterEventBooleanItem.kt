package com.example.holders.registerEvent

import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.EventRegisterFieldData
import com.example.databinding.ItemRegisterEventBooleanBinding
import com.example.extensions.setRequired

open class RegisterEventBooleanItem(
    private val fieldData: EventRegisterFieldData<Boolean>,
    private val editable: Boolean = true,
    onDataChange: (fieldData: EventRegisterFieldData<*>) -> Unit
) : BaseRegisterItem<ItemRegisterEventBooleanBinding>(fieldData, onDataChange) {

    override fun bind(viewBinding: ItemRegisterEventBooleanBinding, position: Int) {
        viewBinding.apply {
            include.checkbox.apply {
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