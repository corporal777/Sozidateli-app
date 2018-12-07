package com.example.holders

import android.text.InputType
import com.example.R
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_request_input.view.*

open class RequestInputItem(private val hint: String) : Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.etInput.apply {
            hint = this@RequestInputItem.hint
            inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        }
    }

    override fun getLayout() = R.layout.item_request_input
}