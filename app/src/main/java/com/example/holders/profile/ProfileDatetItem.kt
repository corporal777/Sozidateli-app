package com.example.holders.profile

import android.text.InputType
import android.view.MotionEvent
import androidx.fragment.app.FragmentManager
import com.example.R
import com.example.data.models.ProfileField
import com.example.extensions.defaultDateFormatter
import com.example.extensions.defaultServerDateFormatter
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.field_profile.view.*
import java.util.*

class ProfileDatetItem(
        private val profileField: ProfileField,
        private val fragmentManager: FragmentManager
) : ProfileFieldItem(profileField) {

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

            datePickerDialog = DatePickerDialog.newInstance({ _, year, monthOfYear, dayOfMonth ->
                val calendar = Calendar.getInstance()
                calendar.set(year, monthOfYear, dayOfMonth)
                profileField.data = defaultServerDateFormatter.format(calendar.timeInMillis)
                editText.setText(defaultServerDateFormatter.format(calendar.timeInMillis))
            }, calendar)

            editText.setOnTouchListener { view, motionEvent ->
                if (motionEvent.action == MotionEvent.ACTION_UP) {
                    datePickerDialog.show(fragmentManager, null)
                }
                return@setOnTouchListener true
            }

        }
    }

    override fun getTextChangeListener() = null

    override fun getInputType() = InputType.TYPE_NULL

    override fun getLayout() = R.layout.field_profile
}