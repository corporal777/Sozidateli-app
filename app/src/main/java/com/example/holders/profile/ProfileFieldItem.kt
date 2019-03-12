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
import com.example.util.SimpleTextWatcher
import com.example.util.Utils
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.field_profile.view.*
import java.util.*

abstract class ProfileFieldItem(private val profileField: ProfileField) : ProfileBaseFieldItem(profileField) {

    protected open var maxLines: Int? = null

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.apply {

            profileField.data?.let {
                when (it) {
                    is String -> {
                        editText.setText(it)
                    }
                }
            }
            profileField.label?.let {
                tvFieldLabel.text = it
            }
            editText.inputType = getInputType()

            getTextChangeListener()?.let {
                editText.addTextChangedListener(it)
            }

            maxLines?.let {
                editText.maxLines = it
            }
        }
    }

    open fun getTextChangeListener(): TextWatcher? {
        return SimpleTextWatcher().setAfterTextChangeRunnable {
            profileField.data = it.toString()
        }
    }

    abstract fun getInputType(): Int


    override fun getLayout() = R.layout.field_profile
}