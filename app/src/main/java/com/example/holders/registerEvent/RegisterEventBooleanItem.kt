package com.example.holders.registerEvent

import android.widget.TextView
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.EventRegisterFieldData
import com.example.databinding.ItemRegisterEventBooleanBinding
import com.example.databinding.ItemRegisterEventCheckboxBinding
import com.example.extensions.setRequired

open class RegisterEventBooleanItem(
    private val fieldData: EventRegisterFieldData<Boolean>,
    onDataChange: (fieldData: EventRegisterFieldData<*>) -> Unit
) : BaseRegisterItem<ItemRegisterEventBooleanBinding>(fieldData, onDataChange) {

    override fun bind(viewBinding: ItemRegisterEventBooleanBinding, position: Int) {
        super.bind(viewBinding, position)
        viewBinding.apply {
            include.checkbox.apply {
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

    override fun getTitleView(binding: ItemRegisterEventBooleanBinding): TextView = binding.textView
    override fun getLayout() = R.layout.item_register_event_boolean
}