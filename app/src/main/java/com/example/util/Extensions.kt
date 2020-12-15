package com.example.util

import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.FragmentManager
import com.example.R
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import onTextChanged
import java.util.*

fun String.firstLetterToUppercase(): String {
    return if (this.isNotBlank())
        this.substring(0, 1).toUpperCase() + this.substring(1).toLowerCase()
    else
        this
}

fun CheckBox.initSwitch(checked: Boolean, onCheckedChanged: (isChecked: Boolean) -> Unit) {
    isChecked = checked
    setOnCheckedChangeListener { _, isChecked -> onCheckedChanged(isChecked) }
}

fun EditText.initInput(text: String?, onTextChanged: (text: CharSequence?) -> Unit) {
    setText(text)
    onTextChanged(onTextChanged)
}

fun FragmentManager.showDatePicker(
        currentDate: String?,
        onDateSelected: (date: Long) -> Unit
) {
    var selection = if (!currentDate.isNullOrBlank())
        serverDateToMilliseconds(currentDate, DATE_FORMAT_SHORT_MONTH_FULL_YEAR)
    else {
        val current = Calendar.getInstance()
        current.add(Calendar.YEAR, -14)
        current.timeInMillis
    }
    val timezone = TimeZone.getDefault()
    selection += timezone.getOffset(selection)
    val endDate = Calendar.getInstance()
    endDate.add(Calendar.YEAR, -14)

    val picker = MaterialDatePicker
            .Builder
            .datePicker()
            .setTitleText(R.string.profile_birthday)
            .setTheme(R.style.DatePickerStyle)
            .setSelection(selection)
            .setCalendarConstraints(
                    CalendarConstraints.Builder()
                            .setEnd(endDate.timeInMillis)
                            .setOpenAt(selection)
                            .setValidator(WeekDayValidator()).build()
            )
            .build()
    picker.addOnPositiveButtonClickListener {
        onDateSelected.invoke(it)
    }
    picker.show(this, "")
}