package com.example.holders.profile

import com.example.R
import com.example.data.models.ProfileField
import com.example.data.models.Type
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder

class ProfileFieldDividerItem(): Item() {


    override fun bind(viewHolder: ViewHolder, position: Int) {
    }

    override fun getLayout(): Int {
        return R.layout.field_divider
    }
}