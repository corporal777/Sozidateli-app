package com.example.holders

import android.widget.EditText
import com.example.R
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_chat_list_header.*

class ChatListHeaderItem(
        private val onInputClick: () -> Unit,
        private val onFilterClick: () -> Unit
) : Item(-2L) {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            btnChats.isSelected = true

            (etSearch as EditText).apply {
                setOnKeyListener(null)
                isFocusable = false
                isFocusableInTouchMode = false
                isLongClickable = false

                setOnClickListener { onInputClick() }
            }
        }
    }

    override fun getLayout() = R.layout.item_chat_list_header
}