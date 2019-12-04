package com.example.holders

import com.example.R
import com.example.data.models.Interest
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_profile_data_interest.*

class ProfileDataInterestItem(
        private val interest: Interest
) : Item() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            tvInterest.text = interest.value
        }
    }

    override fun getLayout() = R.layout.item_profile_data_interest
}