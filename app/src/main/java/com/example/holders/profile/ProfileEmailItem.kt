package com.example.holders.profile

import android.text.InputType
import android.text.TextWatcher
import com.example.R
import com.example.data.models.ProfileField
import com.example.util.AuthValidateUtil
import com.example.util.SimpleTextWatcher
import com.xwray.groupie.kotlinandroidextensions.ViewHolder

class ProfileEmailItem(private val profileField: ProfileField) : ProfileFieldItem(profileField) {


    override var maxLines: Int? = 1

    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)
    }

    override fun getTextChangeListener(): TextWatcher? {
            return SimpleTextWatcher().setAfterTextChangeRunnable {
                profileField.data = it.toString()
                profileField.isValid = AuthValidateUtil.isValidEmail(it.toString())
            }
        }

    override fun getInputType() = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

    override fun getLayout() = R.layout.field_profile
}