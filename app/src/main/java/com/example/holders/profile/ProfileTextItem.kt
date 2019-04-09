package com.example.holders.profile

import android.text.InputType
import com.example.R
import com.example.data.models.ProfileField
import com.xwray.groupie.kotlinandroidextensions.ViewHolder

class ProfileTextItem(private val profileField: ProfileField) : ProfileFieldItem(profileField) {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)
    }

    override fun getInputType() = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or InputType.TYPE_TEXT_FLAG_MULTI_LINE

    override fun getLayout() = R.layout.field_profile
}