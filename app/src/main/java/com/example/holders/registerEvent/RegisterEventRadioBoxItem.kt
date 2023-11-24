package com.example.holders.registerEvent

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import com.example.R
import com.example.data.models.EventRegisterFieldData
import com.example.databinding.ItemRegisterEventRadioBinding

open class RegisterEventRadioBoxItem(
    private val fieldData: EventRegisterFieldData<String>,
    private val editable: Boolean = true,
    onDataChange: (fieldData: EventRegisterFieldData<*>) -> Unit
) : BaseRegisterItem<ItemRegisterEventRadioBinding>(fieldData, onDataChange) {

    override fun bind(viewBinding: ItemRegisterEventRadioBinding, position: Int) {
        val values = field.values ?: emptyList()
        viewBinding.apply {
            radioGroup.apply {
                isEnabled = editable
                removeAllViews()
                values.forEachIndexed { index, value ->
                    addView(
                        createRadioButton(index, value),
                        ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                    )
                }

                setOnCheckedChangeListener { _, id ->
                    fieldData.value = values[id]
                    onDataChange()
                }
            }
        }
    }

    private fun RadioGroup.createRadioButton(index: Int, value: String): RadioButton {
        val button = (LayoutInflater.from(context)
            .inflate(R.layout.item_radio_button, this, false) as RadioButton)
        return button.apply {
            isEnabled = editable
            id = index
            text = value

            fieldData.value?.let {
                if (it == value) isChecked = true
            }
        }
    }

    override fun getLayout() = R.layout.item_register_event_radio
}