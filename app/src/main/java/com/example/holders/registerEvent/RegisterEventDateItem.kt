package com.example.holders.registerEvent

import android.text.TextWatcher
import com.example.R
import com.example.data.models.EventRegisterField
import com.example.data.models.EventRegisterFieldData
import com.example.extensions.*
import com.example.util.DATE_STRING_FORMAT_SHORT_MONTH_FULL_YEAR
import com.example.util.DATE_TIME_STRING_FORMAT_SHORT_MONTH_FULL_YEAR
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import initAsDatePicker
import initAsDateTimePicker
import kotlinx.android.synthetic.main.item_register_event_input.*
import onTextChanged


open class RegisterEventDateItem(
        private val fieldData: EventRegisterFieldData<String>,
        private val editable: Boolean = true,
        onDataChange: (fieldData: EventRegisterFieldData<*>) -> Unit
) : BaseRegisterItem(fieldData, onDataChange) {

    private val textChangeListener: (CharSequence?) -> Unit = {
        fieldData.value = if (field.type == EventRegisterField.Type.DATE) it?.toString()?.formatToDefaultServerDate()
        else it?.toString()?.parseAndFormat(defaultDateTimeFormatter, defaultServerDateTimeFormatter)
        onDataChange()
    }

    private var textWatcher: TextWatcher? = null

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            textInputEditText.apply {
                isEnabled = editable
                val valueString = fieldData.value
                val date = valueString?.let { defaultServerDateFormatter.parse(it) }
                val value: String?
                when (field.type) {
                    EventRegisterField.Type.DATE -> {
                        value = valueString?.formatToDefaultDate()
                        textInputLayout.initAsDatePicker(date) { year, month, day ->
                            String.format(DATE_STRING_FORMAT_SHORT_MONTH_FULL_YEAR, day, month + 1, year)
                        }
                    }
                    EventRegisterField.Type.DATETIME -> {
                        value = valueString?.parseAndFormat(defaultServerDateTimeFormatter, defaultDateTimeFormatter)
                        textInputLayout.initAsDateTimePicker(date) { year, month, day, hour, minute ->
                            String.format(DATE_TIME_STRING_FORMAT_SHORT_MONTH_FULL_YEAR, day, month + 1, year, hour, minute)
                        }
                    }
                    else -> throw IllegalArgumentException("Wrong field type ${field.type} for RegisterEventDateItem")
                }

                hint = field.description
                setText(value)
                textWatcher = onTextChanged(textChangeListener)
            }

            textInputLayout.isEnabled = editable
        }
    }

    override fun unbind(viewHolder: GroupieViewHolder) {
        viewHolder.apply {
            textInputEditText.apply {
                textWatcher?.let { removeTextChangedListener(it) }
            }
        }
        super.unbind(viewHolder)
    }

    override fun getLayout() = R.layout.item_register_event_date
}