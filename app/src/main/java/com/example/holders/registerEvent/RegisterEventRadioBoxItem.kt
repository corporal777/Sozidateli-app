package com.example.holders.registerEvent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import com.example.app.R
import com.example.data.models.eventRegister.EventRegisterField
import com.example.app.databinding.ItemRegisterEventRadioBinding

open class RegisterEventRadioBoxItem(
    private val fieldData: EventRegisterField<String>,
    onDataChange: (fieldData: EventRegisterField<*>) -> Unit
) : BaseRegisterItem<ItemRegisterEventRadioBinding>(fieldData, onDataChange) {

    override fun bind(viewBinding: ItemRegisterEventRadioBinding, position: Int) {
        super.bind(viewBinding, position)
        val values = field.parameters?.options ?: emptyList()
        viewBinding.apply {
            radioGroup.apply {
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
                    showError(false)
                }
            }
        }
    }

    private fun RadioGroup.createRadioButton(index: Int, value: String): RadioButton {
        val button = (LayoutInflater.from(context)
            .inflate(R.layout.item_radio_button, this, false) as RadioButton)
        return button.apply {
            id = index
            text = value

            fieldData.value?.let {
                if (it == value) isChecked = true
            }
        }
    }

    override fun getTitleView(binding: ItemRegisterEventRadioBinding): TextView = binding.textView
    override fun getLayout() = R.layout.item_register_event_radio
    override fun initializeViewBinding(view: View) = ItemRegisterEventRadioBinding.bind(view)
}