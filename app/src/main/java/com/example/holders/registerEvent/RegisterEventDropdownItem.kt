package com.example.holders.registerEvent

import android.view.View
import android.widget.TextView
import com.example.app.R
import com.example.app.databinding.ItemRegisterEventSelectorBinding
import com.example.data.models.eventRegister.EventRegisterField
import com.example.common.extensions.initDropDownView

open class RegisterEventDropdownItem(
    private val fieldData: EventRegisterField<String>,
    onDataChange: (fieldData: EventRegisterField<*>) -> Unit
) : BaseRegisterInputItem<ItemRegisterEventSelectorBinding>(fieldData, onDataChange) {


    override fun bind(viewBinding: ItemRegisterEventSelectorBinding, position: Int) {
        super.bind(viewBinding, position)
        val values = field.parameters?.options ?: emptyList()
        viewBinding.apply {
            autoCompleteTextView.apply {
                initDropDownView(
                    this,
                    values,
                    fieldData.value,
                    if (field.isRequired) null else resources.getString(R.string.search_filters_not_chosen),
                    { it },
                    {
                        fieldData.value = it
                        onDataChange()
                        showError(false)
                    }
                )
                hint = fieldData.field.description
            }
        }
    }

    override fun getInputView(binding: ItemRegisterEventSelectorBinding): View = binding.autoCompleteTextView
    override fun getErrorFrameView(binding: ItemRegisterEventSelectorBinding): View = binding.viewInputError
    override fun getTitleView(binding: ItemRegisterEventSelectorBinding): TextView = binding.textView
    override fun getLayout() = R.layout.item_register_event_selector
    override fun initializeViewBinding(view: View) = ItemRegisterEventSelectorBinding.bind(view)
}