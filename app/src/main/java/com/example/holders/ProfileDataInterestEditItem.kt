package com.example.holders

import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.UserInterest
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_profile_data_edit_interest.*

class ProfileDataInterestEditItem(
        private val userInterest: UserInterest,
        private val compactBottom: Boolean,
        private val onCheckChanged: () -> Unit
) : Item(userInterest.interest.id?.toLong()?: 0) {

    override fun bind(viewHolder:GroupieViewHolder, position: Int) {
        viewHolder.apply {
            cbInterest.apply {
                text = userInterest.interest.name
                isChecked = userInterest.isUserInterest
                setOnCheckedChangeListener { _, isChecked ->
                    userInterest.isUserInterest = isChecked
                    onCheckChanged()
                }
            }
            divider.isVisible = !compactBottom
        }
    }

    override fun unbind(holder:GroupieViewHolder) {
        holder.cbInterest.apply {
            setOnCheckedChangeListener(null)
        }
        super.unbind(holder)
    }

    override fun getLayout() = R.layout.item_profile_data_edit_interest
}