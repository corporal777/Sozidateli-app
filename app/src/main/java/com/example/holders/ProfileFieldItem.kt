package com.example.holders

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import com.example.R
import com.example.data.models.ProfileField
import com.example.data.models.Type
import com.example.util.Utils
import com.squareup.picasso.Picasso
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.field_profile.view.*
import java.text.SimpleDateFormat
import java.util.*

class ProfileFieldItem(private val profileField: ProfileField) : Item() {


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
                        editText.setText(Utils.defaultDataFormatter.format(it))
                        calendar.timeInMillis = it
                    }
                }
            }

            profileField.label?.let {
                tvFieldLabel.text = it
            }

            switchView.visibility = if (profileField.isShowOnlyProfile) View.VISIBLE else View.GONE


            var inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or InputType.TYPE_TEXT_FLAG_MULTI_LINE

            when (profileField.type) {
                Type.DATE -> {
                    inputType = InputType.TYPE_NULL
                    editText.setOnClickListener { view ->
                        datePickerDialog.show()
                    }
                    datePickerDialog = DatePickerDialog(editText.context, DatePickerDialog.OnDateSetListener { datePicker, year, monthOfYear, dayOfMonth ->
                        val calendar = Calendar.getInstance()
                        calendar.set(year, monthOfYear, dayOfMonth)
                        profileField.data = calendar.timeInMillis
                        editText.setText(Utils.defaultDataFormatter.format(calendar.timeInMillis))
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

    fun getField() = profileField

    override fun getLayout() = R.layout.field_profile
}