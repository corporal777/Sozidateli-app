package com.example.holders.registerEvent

import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.app.R
import com.example.app.databinding.ItemRegisterEventBooleanBinding
import com.example.data.models.EventRegisterFieldData
import com.example.extensions.setRequired

open class RegisterEventBooleanItem(
    private val fieldData: EventRegisterFieldData<Boolean>,
    onDataChange: (fieldData: EventRegisterFieldData<*>) -> Unit
) : BaseRegisterItem<ItemRegisterEventBooleanBinding>(fieldData, onDataChange) {

    override fun bind(viewBinding: ItemRegisterEventBooleanBinding, position: Int) {
        super.bind(viewBinding, position)
        viewBinding.apply {
            checkbox.apply {
                setText(field.name)
                setChecked(fieldData.value ?: false)
                setOnCheckedListener { isChecked ->
                    fieldData.value = isChecked
                    onDataChange()
                    this@RegisterEventBooleanItem.showError(false)
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
    override fun initializeViewBinding(view: View) = ItemRegisterEventBooleanBinding.bind(view)
}