package com.example.holders.redesign

import android.widget.TextView
import com.example.R
import com.example.data.models.EventActivityModel
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_lecture.*
import kotlinx.android.synthetic.main.item_screen_header_label.*

class ScreenHeaderItem (
        private val label: String
) : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.tvHeaderLabel.text = label
    }


    fun changeVisibility(value : Float, textView : TextView){
        textView.alpha = value
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int, payloads: MutableList<Any>) {
        val payload = payloads.firstOrNull()
        if (payload == null) super.bind(viewHolder, position, payloads)
        else {
            if (payload is Float) {
                changeVisibility(payload, viewHolder.tvHeaderLabel)
            }
        }

    }

    override fun getLayout() = R.layout.item_screen_header_label
}