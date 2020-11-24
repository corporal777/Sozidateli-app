package com.example.holders.registerEvent

import android.text.TextWatcher
import com.example.R
import com.example.data.models.EventPassport
import com.example.data.models.EventRegisterFieldData
import com.example.extensions.defaultDateFormatter
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.formatToDefaultServerDate
import com.example.extensions.setRequired
import com.example.util.DATE_STRING_FORMAT_SHORT_MONTH_FULL_YEAR
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import initAsDatePicker
import kotlinx.android.synthetic.main.item_register_event_passport.*
import onTextChanged
import java.util.*

class RegisterEventPassportItem(
        private val fieldData: EventRegisterFieldData<EventPassport>,
        private val editable: Boolean = true,
        onDataChange: (fieldData: EventRegisterFieldData<*>) -> Unit
) : BaseRegisterItem(fieldData, onDataChange) {

    private var serialTextWatcher: TextWatcher? = null
    private var numberTextWatcher: TextWatcher? = null
    private var agencyTextWatcher: TextWatcher? = null
    private var dateTextWatcher: TextWatcher? = null
    private var codeTextWatcher: TextWatcher? = null

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            val passport = fieldData.value ?: EventPassport().apply { fieldData.value = this }

            etSerial.apply {
                isEnabled = editable
                hint = hint?.setRequired(fieldData.field.required)
                setText(passport.serial)
                serialTextWatcher = onTextChanged {
                    passport.serial = it?.toString()
                    onDataChange()
                }
            }

            etNumber.apply {
                isEnabled = editable
                hint = hint?.setRequired(fieldData.field.required)
                setText(passport.num)
                numberTextWatcher = onTextChanged {
                    passport.num = it?.toString()
                    onDataChange()
                }
            }

            etAgency.apply {
                isEnabled = editable
                hint = hint?.setRequired(fieldData.field.required)
                setText(passport.org)
                agencyTextWatcher = onTextChanged {
                    passport.org = it?.toString()
                    onDataChange()
                }
            }

            val date = passport.date?.let { defaultServerDateFormatter.parse(it) }
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
                    passport.date = it?.toString()?.formatToDefaultServerDate()
                    onDataChange()
                }
            }

            etCode.apply {
                isEnabled = editable
                hint = hint?.setRequired(fieldData.field.required)
                setText(passport.kod)
                codeTextWatcher = onTextChanged {
                    passport.kod = it?.toString()
                    onDataChange()
                }
            }
        }
    }

    override fun unbind(viewHolder: GroupieViewHolder) {
        viewHolder.apply {
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