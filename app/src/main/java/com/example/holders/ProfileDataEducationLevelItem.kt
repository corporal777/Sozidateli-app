package com.example.holders

import com.example.R
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_profile_data_education_level.*

class ProfileDataEducationLevelItem(
        private val education: String?
) : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            tvEducationLevel.text = education
        }
    }

    override fun getLayout() = R.layout.item_profile_data_education_level
}