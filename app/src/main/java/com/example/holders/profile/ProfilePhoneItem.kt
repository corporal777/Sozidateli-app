package com.example.holders.profile

import com.example.R
import com.example.data.models.ProfileField
import com.example.util.SimpleTextWatcher
import com.hbb20.CountryCodePicker
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.field_profile_phone.view.*


class ProfilePhoneItem(private val profileField: ProfileField) : ProfileBaseFieldItem(profileField) {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)
        viewHolder.itemView.apply {
            ccp.registerCarrierNumberEditText(editText)
            ccp.setPhoneNumberValidityChangeListener(CountryCodePicker.PhoneNumberValidityChangeListener {
                profileField.isValid = it
                if (it) {
                    profileField.data = ccp.fullNumberWithPlus
                }
            })

            editText.addTextChangedListener(SimpleTextWatcher().setAfterTextChangeRunnable {
                profileField.data = ccp.fullNumberWithPlus
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