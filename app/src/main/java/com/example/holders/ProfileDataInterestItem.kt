package com.example.holders

import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.Interest
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_profile_data_interest.*

class ProfileDataInterestItem(
        private val interest: Interest,
        private val compactBottom: Boolean
) : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            tvInterest.text = interest.value
            divider.isVisible = !compactBottom
        }
    }

    override fun getLayout() = R.layout.item_profile_data_interest
}