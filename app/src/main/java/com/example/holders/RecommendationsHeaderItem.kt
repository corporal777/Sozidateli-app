package com.example.holders

import com.example.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_recommendations_header.*

class RecommendationsHeaderItem(
    private val onSearchClick: () -> Unit,
    private val onOrganizationsClick: () -> Unit,
    private val onMyEventsClick: () -> Unit
) : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            etSearch.setOnClickListener { onSearchClick() }
            btnOrganizations.setOnClickListener { onOrganizationsClick() }
            btnMyEvents.setOnClickListener { onMyEventsClick() }
        }
    }

    override fun getLayout() = R.layout.item_recommendations_header
}