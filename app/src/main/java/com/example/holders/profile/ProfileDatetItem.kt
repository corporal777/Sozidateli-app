package com.example.holders.profile

import android.app.DatePickerDialog
import android.text.InputType
import android.view.MotionEvent
import com.example.R
import com.example.data.models.ProfileField
import com.example.extensions.defaultDateFormatter
import com.example.extensions.defaultServerDateFormatter
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.field_profile.view.*
import java.util.*

class ProfileDatetItem(private val profileField: ProfileField) : ProfileFieldItem(profileField) {

    private lateinit var datePickerDialog: DatePickerDialog

    private var calendar = Calendar.getInstance()

    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)
        viewHolder.itemView.apply {
            profileField.data?.let {
                if (it is Long) {
                    editText.setText(defaultDateFormatter.format(it))
                    calendar.timeInMillis = it
                }
            }

            datePickerDialog = DatePickerDialog(editText.context, DatePickerDialog.OnDateSetListener { datePicker, year, monthOfYear, dayOfMonth ->
                val calendar = Calendar.getInstance()
                calendar.set(year, monthOfYear, dayOfMonth)
                profileField.data = defaultServerDateFormatter.format(calendar.timeInMillis)
                editText.setText(defaultServerDateFormatter.format(calendar.timeInMillis))
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

            editText.setOnTouchListener { view, motionEvent ->
                if (motionEvent.action == MotionEvent.ACTION_UP) {
                    datePickerDialog.show()
                }
                return@setOnTouchListener true
            }

        }
    }

    override fun getTextChangeListener() = null

    override fun getInputType() = InputType.TYPE_NULL

    override fun getLayout() = R.layout.field_profile
}