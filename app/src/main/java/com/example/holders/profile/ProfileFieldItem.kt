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

open class ProfileFieldItem(private val profileField: ProfileField) : ProfileBaseFieldItem(profileField) {


    private lateinit var datePickerDialog: DatePickerDialog

    private var calendar = Calendar.getInstance()

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.apply {

            profileField.data?.let {
                when (it) {
                    is String -> {
                        editText.setText(it)
                    }
                    is Long -> {
                        editText.setText(Utils.defaultDateFormatter.format(it))
                        calendar.timeInMillis = it
                    }
                }
            }

            profileField.label?.let {
                tvFieldLabel.text = it
            }


            var inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or InputType.TYPE_TEXT_FLAG_MULTI_LINE
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            setPadding( paddingLeft,context.resources.getDimensionPixelSize(R.dimen.profile_margin_between_field),paddingRight,paddingBottom)

            visibility =View.VISIBLE

            when (profileField.type) {
                Type.DATE -> {
                    inputType = InputType.TYPE_NULL
                    editText.setOnTouchListener { view, motionEvent ->
                        if (motionEvent.action == MotionEvent.ACTION_UP) {
                            datePickerDialog.show()
                        }
                        return@setOnTouchListener true
                    }
                    datePickerDialog = DatePickerDialog(editText.context, DatePickerDialog.OnDateSetListener { datePicker, year, monthOfYear, dayOfMonth ->
                        val calendar = Calendar.getInstance()
                        calendar.set(year, monthOfYear, dayOfMonth)
                        profileField.data = Utils.defaultServerDateFormatter.format(calendar.timeInMillis)
                        editText.setText(Utils.defaultServerDateFormatter.format(calendar.timeInMillis))
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                }
                Type.EMAIL -> {
                    inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                }
                Type.PASSWORD -> {
                    inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                }
                Type.PHONE -> {
                    inputType = InputType.TYPE_CLASS_PHONE
                }
                Type.TEXT -> {
                }
                Type.SUPPORT -> {
                    layoutParams.height = 0
                    setPadding( 0,0,0,0)
                    visibility =View.GONE
                }
            }

            editText.inputType = inputType

            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {}
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (profileField.type != Type.DATE) {
                        profileField.data = p0.toString()
                    }
                }
            })

        }
    }

    override fun getLayout() = R.layout.field_profile
}