package com.example.holders.profile

import com.example.R
import com.example.data.models.ProfileField
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder

abstract class ProfileBaseFieldItem(var field:ProfileField): Item(field.id.hashCode().toLong()) {

    override fun bind(viewHolder: ViewHolder, position: Int) {

    }

    override fun getLayout() = R.layout.field_profile
}