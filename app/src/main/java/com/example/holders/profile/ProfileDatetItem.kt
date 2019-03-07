package com.example.holders.profile

import android.app.ActionBar
import android.app.DatePickerDialog
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.example.R
import com.example.data.models.ProfileField
import com.example.data.models.Type
import com.example.util.Utils
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
                if(it is Long){
                    editText.setText(Utils.defaultDateFormatter.format(it))
                    calendar.timeInMillis = it
                }
            }

            datePickerDialog = DatePickerDialog(editText.context, DatePickerDialog.OnDateSetListener { datePicker, year, monthOfYear, dayOfMonth ->
                val calendar = Calendar.getInstance()
                calendar.set(year, monthOfYear, dayOfMonth)
                profileField.data = Utils.defaultServerDateFormatter.format(calendar.timeInMillis)
                editText.setText(Utils.defaultServerDateFormatter.format(calendar.timeInMillis))
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