package com.example.holders.registerEvent

import android.widget.TextView
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.EventRegisterFieldData
import com.example.databinding.ItemRegisterEventCheckboxBinding
import com.example.databinding.ItemRegisterEventSelectorBinding
import com.example.extensions.initDropDownView

open class EventRegistrationSelectBoxItem(
    private val fieldData: EventRegisterFieldData<String>,
    onDataChange: (fieldData: EventRegisterFieldData<*>) -> Unit
) : BaseRegisterItem<ItemRegisterEventSelectorBinding>(fieldData, onDataChange) {


    override fun bind(viewBinding: ItemRegisterEventSelectorBinding, position: Int) {
        super.bind(viewBinding, position)
        val values = field.values ?: emptyList()
        viewBinding.apply {
            autoCompleteTextView.apply {
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

    override fun getTitleView(binding: ItemRegisterEventSelectorBinding): TextView = binding.textView
    override fun getLayout() = R.layout.item_register_event_selector
}