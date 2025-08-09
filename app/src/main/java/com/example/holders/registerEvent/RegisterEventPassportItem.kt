package com.example.holders.registerEvent

import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import com.example.app.R
import com.example.app.databinding.ItemRegisterEventPassportBinding
import com.example.data.models.EventPassport
import com.example.data.models.eventRegister.EventRegisterField
import com.example.common.defaultDateFormatter
import com.example.common.defaultServerDateFormatter
import com.example.common.formatToDefaultServerDate
import com.example.common.extensions.initAsDatePicker
import com.example.common.DATE_STRING_FORMAT_SHORT_MONTH_FULL_YEAR
import com.example.util.getColorStateList
import com.xwray.groupie.viewbinding.GroupieViewHolder
import java.util.Date

class RegisterEventPassportItem(
    private val fieldData: EventRegisterField<EventPassport>,
    onDataChange: (fieldData: EventRegisterField<*>) -> Unit
) : BaseRegisterItem<ItemRegisterEventPassportBinding>(fieldData, onDataChange) {

    private var serialTextWatcher: TextWatcher? = null
    private var numberTextWatcher: TextWatcher? = null
    private var agencyTextWatcher: TextWatcher? = null
    private var dateTextWatcher: TextWatcher? = null
    private var codeTextWatcher: TextWatcher? = null


    override fun bind(viewBinding: ItemRegisterEventPassportBinding, position: Int) {
        super.bind(viewBinding, position)
        viewBinding.apply {
            val passport = fieldData.value ?: EventPassport().apply { fieldData.value = this }

            etSerial.apply {
                setViewBackground(this)
                setText(passport.series)
                serialTextWatcher = onTextChanged {
                    passport.series = it?.toString()
                    onDataChange()
                }
            }

            etNumber.apply {
                setViewBackground(this)
                setText(passport.number)
                numberTextWatcher = onTextChanged {
                    passport.number = it?.toString()
                    onDataChange()
                }
            }

            etAgency.apply {
                setViewBackground(this)
                setText(passport.issuedBy)
                agencyTextWatcher = onTextChanged {
                    passport.issuedBy = it?.toString()
                    onDataChange()
                }
            }



            etDate.apply {
                val date = passport.issuedDate?.let { defaultServerDateFormatter.parse(it) }
                tilDate.initAsDatePicker(date, maxDate = Date()) { year, month, day ->
                    String.format(DATE_STRING_FORMAT_SHORT_MONTH_FULL_YEAR, day, month + 1, year)
                }

                setViewBackground(this)
                setText(date?.let { defaultDateFormatter.format(it) })
                dateTextWatcher = onTextChanged {
                    passport.issuedDate = it?.toString()?.formatToDefaultServerDate()
                    onDataChange()
                }
            }

            etCode.apply {
                setViewBackground(this)
                setText(passport.issuedDepartment)
                codeTextWatcher = onTextChanged {
                    passport.issuedDepartment = it?.toString()
                    onDataChange()
                }
            }
        }
    }

    private fun setViewBackground(view : View){
        view.apply {
            backgroundTintList = if (field.isRequired)
                getColorStateList(R.color.background_input_event_register_required)
            else getColorStateList(R.color.background_input_event_register)
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

    override fun getTitleView(binding: ItemRegisterEventPassportBinding): TextView = binding.textView
    override fun getLayout() = R.layout.item_register_event_passport
    override fun initializeViewBinding(view: View) = ItemRegisterEventPassportBinding.bind(view)
}