package com.example.holders

import com.example.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_my_events_header.*
import kotlinx.android.synthetic.main.item_organizations_header.*

class OrganizationsHeaderItem(
        private val onFilterClickListener: OnFilterClickListener
) : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            btnFavorites.setOnClickListener { onFilterClickListener.onFavoritesClick() }
        }
    }

    override fun getLayout() = R.layout.item_organizations_header

    interface OnFilterClickListener {
        fun onFavoritesClick()
    }
}