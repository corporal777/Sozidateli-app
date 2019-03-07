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

class ProfilePasswordItem(private val profileField: ProfileField) : ProfileFieldItem(profileField) {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)
    }

    override fun getInputType() = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

    override fun getLayout() = R.layout.field_profile
}