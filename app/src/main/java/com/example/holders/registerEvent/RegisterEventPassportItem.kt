package com.example.holders.registerEvent

import android.text.TextWatcher
import com.example.R
import com.example.data.models.EventPassport
import com.example.data.models.EventRegisterFieldData
import com.example.databinding.ItemRegisterEventPassportBinding
import com.example.extensions.defaultDateFormatter
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.formatToDefaultServerDate
import com.example.extensions.setRequired
import com.example.util.DATE_STRING_FORMAT_SHORT_MONTH_FULL_YEAR
import com.xwray.groupie.databinding.GroupieViewHolder
import com.example.extensions.initAsDatePicker
import com.example.extensions.onTextChanged
import java.util.*

class RegisterEventPassportItem(
    private val fieldData: EventRegisterFieldData<EventPassport>,
    private val editable: Boolean = true,
    onDataChange: (fieldData: EventRegisterFieldData<*>) -> Unit
) : BaseRegisterItem<ItemRegisterEventPassportBinding>(fieldData, onDataChange) {

    private var serialTextWatcher: TextWatcher? = null
    private var numberTextWatcher: TextWatcher? = null
    private var agencyTextWatcher: TextWatcher? = null
    private var dateTextWatcher: TextWatcher? = null
    private var codeTextWatcher: TextWatcher? = null


    override fun bind(viewBinding: ItemRegisterEventPassportBinding, position: Int) {
        viewBinding.apply {
            val passport = fieldData.value ?: EventPassport().apply { fieldData.value = this }

            etSerial.apply {
                isEnabled = editable
                hint = hint?.setRequired(fieldData.field.required)
                setText(passport.series)
                serialTextWatcher = onTextChanged {
                    passport.series = it?.toString()
                    onDataChange()
                }
            }

            etNumber.apply {
                isEnabled = editable
                hint = hint?.setRequired(fieldData.field.required)
                setText(passport.number)
                numberTextWatcher = onTextChanged {
                    passport.number = it?.toString()
                    onDataChange()
                }
            }

            etAgency.apply {
                isEnabled = editable
                hint = hint?.setRequired(fieldData.field.required)
                setText(passport.issuedBy)
                agencyTextWatcher = onTextChanged {
                    passport.issuedBy = it?.toString()
                    onDataChange()
                }
            }

            val date = passport.issuedDate?.let { defaultServerDateFormatter.parse(it) }
            tilDate.apply {
                isEnabled = editable
                initAsDatePicker(date, maxDate = Date()) { year, month, day ->
                    String.format(DATE_STRING_FORMAT_SHORT_MONTH_FULL_YEAR, day, month + 1, year)
                }
            }

            etDate.apply {
                isEnabled = editable
                hint = hint?.setRequired(fieldData.field.required)
                setText(date?.let { defaultDateFormatter.format(it) })
                dateTextWatcher = onTextChanged {
                    passport.issuedDate = it?.toString()?.formatToDefaultServerDate()
                    onDataChange()
                }
            }

            etCode.apply {
                isEnabled = editable
                hint = hint?.setRequired(fieldData.field.required)
                setText(passport.issuedDepartment)
                codeTextWatcher = onTextChanged {
                    passport.issuedDepartment = it?.toString()
                    onDataChange()
                }
            }
        }
    }

    override fun unbind(viewHolder: GroupieViewHolder<ItemRegisterEventPassportBinding>) {
        viewHolder.binding.apply {
            etSerial.removeTextChangedListener(serialTextWatcher)
            etNumber.removeTextChangedListener(numberTextWatcher)
            etAgency.removeTextChangedListener(agencyTextWatcher)
            etDate.removeTextChangedListener(dateTextWatcher)
            etCode.removeTextChangedListener(codeTextWatcher)
        }
        super.unbind(viewHolder)
    }

    override fun getLayout() = R.layout.item_register_event_passport
}