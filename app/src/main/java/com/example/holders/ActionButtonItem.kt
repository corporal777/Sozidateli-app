package com.example.holders

import android.view.View
import android.widget.LinearLayout
import com.example.R
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_action_button.view.*

class ActionButtonItem(
        private val text: String,
        private val clickListener: View.OnClickListener,
        private val marginTop: Int = -1
) : Item() {

    override fun bind(viewHolder:GroupieViewHolder, position: Int) {
        viewHolder.itemView.apply {
            btn.text = text
            btn.setOnClickListener(clickListener)
            if (marginTop >= 0) {
                (btn.layoutParams as LinearLayout.LayoutParams).topMargin = marginTop
            }
        }
    }

    override fun getLayout() = R.layout.item_action_button
}