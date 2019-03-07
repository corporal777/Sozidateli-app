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

class ProfileSupportItem(private val profileField: ProfileField) : ProfileFieldItem(profileField) {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.apply {
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            setPadding( paddingLeft,context.resources.getDimensionPixelSize(R.dimen.profile_margin_between_field),paddingRight,paddingBottom)
            layoutParams.height = 0
            setPadding( 0,0,0,0)
            visibility =View.GONE
        }
    }

    override fun getInputType() = 0

    override fun getLayout() = R.layout.field_profile
}