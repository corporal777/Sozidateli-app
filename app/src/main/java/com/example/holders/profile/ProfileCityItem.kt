package com.example.holders.profile

import android.text.TextWatcher
import android.widget.Toast
import com.example.R
import com.example.data.models.ProfileField
import com.example.util.SimpleTextWatcher
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.field_city.view.*
import kotlinx.android.synthetic.main.field_profile.view.tvFieldLabel

 class ProfileCityItem(private val profileField: ProfileField) : ProfileBaseFieldItem(profileField) {


    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)
        viewHolder.itemView.apply {
            profileField.label?.let {
                tvFieldLabel.text = it
            }
            profileField.data?.let {
                if(it is String) city.setTextWithoutListen(it)
            }
            city.onItemSelected = {
               profileField.data = it.unrestricted_value
            }
        }
    }

    override fun getLayout() = R.layout.field_city
}