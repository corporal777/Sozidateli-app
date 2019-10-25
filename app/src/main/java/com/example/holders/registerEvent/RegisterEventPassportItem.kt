package com.example.holders.registerEvent

import android.text.TextWatcher
import com.example.R
import com.example.data.models.EventPassport
import com.example.data.models.RegisterEventFieldData
import com.example.extensions.defaultDateFormatter
import com.example.extensions.formatToDefaultServerDate
import com.example.util.DATE_STRING_FORMAT_SHORT_MONTH_FULL_YEAR
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import initAsDatePicker
import kotlinx.android.synthetic.main.item_register_event_passport.*
import onTextChanged

class RegisterEventPassportItem(
        private val fieldData: RegisterEventFieldData<EventPassport>
) : BaseRegisterItem(fieldData) {

    private var serialTextWatcher: TextWatcher? = null
    private var numberTextWatcher: TextWatcher? = null
    private var agencyTextWatcher: TextWatcher? = null
    private var dateTextWatcher: TextWatcher? = null
    private var codeTextWatcher: TextWatcher? = null

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            val passport = fieldData.value ?: EventPassport().apply { fieldData.value = this }

            etSerial.apply {
                setText(passport.series)
                serialTextWatcher = onTextChanged { passport.series = it?.toString() }
            }

            etNumber.apply {
                setText(passport.number)
                numberTextWatcher = onTextChanged { passport.number = it?.toString() }
            }

            etAgency.apply {
                setText(passport.agency)
                agencyTextWatcher = onTextChanged { passport.agency = it?.toString() }
            }

            val date = passport.date?.let { defaultDateFormatter.parse(it) }
            tilDate.apply {
                initAsDatePicker(date) { year, month, day ->
                    String.format(DATE_STRING_FORMAT_SHORT_MONTH_FULL_YEAR, day, month + 1, year)
                }
            }

            etDate.apply {
                setText(date?.let { defaultDateFormatter.format(it) })
                dateTextWatcher = onTextChanged {
                    passport.date = it?.toString()?.formatToDefaultServerDate()
                }
            }

            etCode.apply {
                setText(passport.code)
                codeTextWatcher = onTextChanged { passport.code = it?.toString() }
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