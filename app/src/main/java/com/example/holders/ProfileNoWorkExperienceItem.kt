package com.example.holders

import com.example.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_no_work_data.*

class ProfileNoWorkExperienceItem(
        private val message: String
) : Item() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            tv_text.apply {
                text = message
            }
        }
    }

    override fun getLayout() = R.layout.item_no_work_data
}