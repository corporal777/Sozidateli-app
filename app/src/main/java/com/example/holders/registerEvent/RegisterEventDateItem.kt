package com.example.holders.registerEvent

import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import com.example.app.R
import com.example.app.databinding.ItemRegisterEventDateBinding
import com.example.data.models.EventFormFieldModel
import com.example.data.models.eventRegister.EventRegisterField
import com.example.common.defaultDateTimeFormatter
import com.example.common.defaultServerDateFormatter
import com.example.common.defaultServerDateTimeFormatter
import com.example.common.formatToDefaultDate
import com.example.common.formatToDefaultServerDate
import com.example.common.extensions.initAsDatePicker
import com.example.common.extensions.initAsDateTimePicker
import com.example.common.parseAndFormat
import com.example.common.DATE_STRING_FORMAT_SHORT_MONTH_FULL_YEAR
import com.example.common.DATE_TIME_STRING_FORMAT_SHORT_MONTH_FULL_YEAR
import com.xwray.groupie.viewbinding.GroupieViewHolder
import java.util.Calendar


open class RegisterEventDateItem(
    private val fieldData: EventRegisterField<String>,
    onDataChange: (fieldData: EventRegisterField<*>) -> Unit
) : BaseRegisterInputItem<ItemRegisterEventDateBinding>(fieldData, onDataChange) {

    private val textChangeListener: (CharSequence?) -> Unit = {
        fieldData.value = if (field.type == EventFormFieldModel.Type.DATE) it?.toString()
            ?.formatToDefaultServerDate()
        else it?.toString()
            ?.parseAndFormat(defaultDateTimeFormatter, defaultServerDateTimeFormatter)
        onDataChange()
        showError(false)
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
                    EventFormFieldModel.Type.DATE -> {
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
                    EventFormFieldModel.Type.DATETIME -> {
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
                    EventFormFieldModel.Type.DATETIMEPLANED -> {
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

    override fun getInputView(binding: ItemRegisterEventDateBinding): View = binding.textInputEditText
    override fun getErrorFrameView(binding: ItemRegisterEventDateBinding): View = binding.viewInputError
    override fun getTitleView(binding: ItemRegisterEventDateBinding): TextView = binding.textView
    override fun getLayout() = R.layout.item_register_event_date
    override fun initializeViewBinding(view: View) = ItemRegisterEventDateBinding.bind(view)
}