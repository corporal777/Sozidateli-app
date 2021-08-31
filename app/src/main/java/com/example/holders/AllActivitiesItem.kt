package com.example.holders

import com.example.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item

class AllActivitiesItem(
        private val onSelect: () -> Unit
): Item(1) {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.setOnClickListener {
                onSelect()
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_all_activities
}