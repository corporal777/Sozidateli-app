package com.example.holders.registerEvent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.app.R
import com.example.data.models.EventRegisterFieldData
import com.example.app.databinding.ItemRegisterEventCheckboxBinding
import com.example.app.databinding.ItemRegisterEventRadioBinding

open class RegisterEventCheckboxItem(
    private val fieldData: EventRegisterFieldData<Set<String>>,
    onDataChange: (fieldData: EventRegisterFieldData<*>) -> Unit
) : BaseRegisterItem<ItemRegisterEventCheckboxBinding>(fieldData, onDataChange) {

    override fun bind(viewBinding: ItemRegisterEventCheckboxBinding, position: Int) {
        super.bind(viewBinding, position)
        val values = field.values ?: emptyList()
        viewBinding.checkGroup.apply {
            removeAllViews()
            values.forEachIndexed { index, value ->
                addView(
                    createCheckbox(index, value),
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                )
            }
        }
    }


    private fun ViewGroup.createCheckbox(index: Int, value: String): CheckBox {
        val checkbox = (LayoutInflater.from(context)
            .inflate(R.layout.item_checkbox, this, false) as CheckBox)
        return checkbox.apply {
            id = index
            text = value

            setOnCheckedChangeListener { _, isChecked ->
                val valuesData = if (fieldData.value == null) {
                    mutableSetOf<String>().apply { fieldData.value = this }
                } else fieldData.value as MutableSet<String>

                if (isChecked) valuesData.add(value) else valuesData.remove(value)
                onDataChange()
                showError(false)
            }

            fieldData.value?.let {
                if (it.contains(value)) isChecked = true
            }
        }
    }

    override fun getTitleView(binding: ItemRegisterEventCheckboxBinding): TextView = binding.textView
    override fun getLayout() = R.layout.item_register_event_checkbox
}