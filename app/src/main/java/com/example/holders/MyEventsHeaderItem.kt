package com.example.holders

import com.example.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_my_events_header.*

class MyEventsHeaderItem(
        private val onFilterClickListener: OnFilterClickListener
) : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            btnAccepted.setOnClickListener { onFilterClickListener.onAcceptedClick() }
            btnPending.setOnClickListener { onFilterClickListener.onPendingClick() }
            btnDeclined.setOnClickListener { onFilterClickListener.onDeclinedClick() }
        }
    }

    override fun getLayout() = R.layout.item_my_events_header

    interface OnFilterClickListener {
        fun onAcceptedClick()
        fun onPendingClick()
        fun onDeclinedClick()
    }
}