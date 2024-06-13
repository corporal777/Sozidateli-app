package com.example.holders.registerEvent

import android.text.TextWatcher
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.EventRegisterField
import com.example.data.models.EventRegisterFieldData
import com.example.databinding.ItemRegisterEventCheckboxBinding
import com.example.databinding.ItemRegisterEventDateBinding
import com.example.extensions.*
import com.example.util.DATE_STRING_FORMAT_SHORT_MONTH_FULL_YEAR
import com.example.util.DATE_TIME_STRING_FORMAT_SHORT_MONTH_FULL_YEAR
import com.xwray.groupie.databinding.GroupieViewHolder
import com.example.extensions.initAsDatePicker
import com.example.extensions.initAsDateTimePicker
import com.example.extensions.onTextChanged
import java.util.*


open class RegisterEventDateItem(
    private val fieldData: EventRegisterFieldData<String>,
    onDataChange: (fieldData: EventRegisterFieldData<*>) -> Unit
) : BaseRegisterItem<ItemRegisterEventDateBinding>(fieldData, onDataChange) {

    private val textChangeListener: (CharSequence?) -> Unit = {
        fieldData.value = if (field.type == EventRegisterField.Type.DATE) it?.toString()
            ?.formatToDefaultServerDate()
        else it?.toString()
            ?.parseAndFormat(defaultDateTimeFormatter, defaultServerDateTimeFormatter)
        onDataChange()
    }

    private var textWatcher: TextWatcher? = null

    override fun bind(viewBinding: ItemRegisterEventDateBinding, position: Int) {
        super.bind(viewBinding, position)
        viewBinding.apply {
            textInputEditText.apply {
                val valueString = fieldData.value
                val date = valueString?.let { defaultServerDateFormatter.parse(it) }
                val value: String?
                when (field.type) {
                    EventRegisterField.Type.DATE -> {
                        value = valueString?.formatToDefaultDate()
                        baseTextInputLayout.initAsDatePicker(date) { year, month, day ->
                            String.format(
                                DATE_STRING_FORMAT_SHORT_MONTH_FULL_YEAR,
                                day,
                                month + 1,
                                year
                            )
                        }
                    }
                    EventRegisterField.Type.DATETIME -> {
                        value = valueString?.parseAndFormat(
                            defaultServerDateTimeFormatter,
                            defaultDateTimeFormatter
                        )
                        baseTextInputLayout.initAsDateTimePicker(date) { year, month, day, hour, minute ->
                            String.format(
                                DATE_TIME_STRING_FORMAT_SHORT_MONTH_FULL_YEAR,
                                day,
                                month + 1,
                                year,
                                hour,
                                minute
                            )
                        }
                    }
                    EventRegisterField.Type.DATETIMEPLANED -> {
                        value = valueString?.parseAndFormat(
                            defaultServerDateTimeFormatter,
                            defaultDateTimeFormatter
                        )
                        baseTextInputLayout.initAsDateTimePicker(
                            date,
                            Calendar.getInstance().time
                        ) { year, month, day, hour, minute ->
                            String.format(
                                DATE_TIME_STRING_FORMAT_SHORT_MONTH_FULL_YEAR,
                                day,
                                month + 1,
                                year,
                                hour,
                                minute
                            )
                        }
                    }
                    else -> throw IllegalArgumentException("Wrong field type ${field.type} for RegisterEventDateItem")
                }

                hint = field.description
                setText(value)
                textWatcher = onTextChanged(textChangeListener)
            }
        }
    }


    override fun unbind(viewHolder: GroupieViewHolder<ItemRegisterEventDateBinding>) {
        viewHolder.binding.apply {
            textInputEditText.apply {
                textWatcher?.let { removeTextChangedListener(it) }
            }
        }
        super.unbind(viewHolder)
    }

    override fun getTitleView(binding: ItemRegisterEventDateBinding): TextView = binding.textView
    override fun getLayout() = R.layout.item_register_event_date
}