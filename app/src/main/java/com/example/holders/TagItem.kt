package com.example.holders

import android.graphics.Color
import android.widget.TextView
import com.example.R
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_tag.view.*

open class TagItem(private val tag: String,private val canSelected:Boolean,private val selected :Boolean, private val onTagSelected: (tag: String,isSelected:Boolean) -> Unit) : Item() {

    private var isSelected: Boolean = false

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.apply {
            tvTag.text = this@TagItem.tag
            this@TagItem.isSelected = selected
            selected(tvTag)

            setOnClickListener {
                if(!canSelected) return@setOnClickListener
                this@TagItem.isSelected = !this@TagItem.isSelected
                selected(tvTag)
            }
        }
    }

    private fun selected(textView: TextView) {
        if (isSelected) {
            textView.setBackgroundResource(R.drawable.background_fill_tag)
            textView.setTextColor(Color.WHITE)
        } else {
            textView.setBackgroundResource(R.drawable.background_empty_tag)
            textView.setTextColor(Color.BLACK)
        }
        onTagSelected(tag,isSelected)
    }

    override fun getLayout() = R.layout.item_tag
}