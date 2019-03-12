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
import kotlinx.android.synthetic.main.field_profile_phone.view.*
import java.util.*
import com.hbb20.CountryCodePicker
import com.hbb20.InternationalPhoneTextWatcher


class ProfilePhoneItem(private val profileField: ProfileField) : ProfileBaseFieldItem(profileField) {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.apply {
            ccp.registerCarrierNumberEditText(editText)
            ccp.setPhoneNumberValidityChangeListener(CountryCodePicker.PhoneNumberValidityChangeListener {
                 profileField.isValid = it
                if(it){
                    profileField.data = ccp.fullNumberWithPlus
                }
            })

            profileField.data?.let {
                ccp.fullNumber = it.toString()
            }

            profileField.label?.let {
                tvFieldLabel.text = it
            }
        }
    }

    override fun getLayout() = R.layout.field_profile_phone
}