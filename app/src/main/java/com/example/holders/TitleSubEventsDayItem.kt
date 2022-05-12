package com.example.holders

import com.example.R
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_title_sub_event.view.*
import java.text.SimpleDateFormat
import java.util.*

open class TitleSubEventsDayItem(private val date:Long) : Item() {
    override fun bind(viewHolder:GroupieViewHolder, position: Int) {
        viewHolder.itemView.apply {
            tvText.text = SimpleDateFormat("EEE dd.MM", Locale.getDefault()).format(date)
        }
    }

    override fun getLayout() = R.layout.item_title_sub_event
}