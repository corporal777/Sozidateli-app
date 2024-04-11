package com.example.ui.views.suggestFieldView.format

import com.example.R
import com.example.databinding.ItemFormatBinding
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem

class EventFormatItem(
    private val itemId: Long?,
    private val format: String?,
    private val hasMask : Boolean,
    private val onFormatClick: (format: String) -> Unit
) : BindableItem<ItemFormatBinding>(itemId ?: 0) {


    override fun bind(viewBinding: ItemFormatBinding, position: Int) {
        viewBinding.apply {
            if (hasMask) {
                tvContent.text = "Искать «" + (format ?: "") + "»"
            } else tvContent.text = format

            root.setOnClickListener {
                onFormatClick.invoke(format ?: "")
            }
        }
    }

    override fun hasSameContentAs(other: Item<*>?): Boolean {
        if (other !is EventFormatItem) return false
        if (format != other.format) return false
        if (hasMask != other.hasMask) return false
        return true
    }

    override fun getLayout(): Int = R.layout.item_format
}