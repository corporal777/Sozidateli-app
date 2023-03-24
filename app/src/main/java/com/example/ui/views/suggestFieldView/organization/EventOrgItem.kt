package com.example.ui.views.suggestFieldView.organization

import com.example.R
import com.example.databinding.ItemFormatBinding
import com.example.ui.views.suggestFieldView.format.EventFormatItem
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem

class EventOrgItem (
    private val itemId: Long?,
    private val format: String?,
    private val onFormatClick: (format: String) -> Unit
) : BindableItem<ItemFormatBinding>(itemId ?: 0) {


    override fun bind(viewBinding: ItemFormatBinding, position: Int) {
        viewBinding.apply {
            if (itemId == null) {
                tvContent.text = "Искать «" + (format ?: "") + "»"
            } else tvContent.text = format

            root.setOnClickListener {
                onFormatClick.invoke(format ?: "")
            }
        }
    }

    override fun hasSameContentAs(other: Item<*>?): Boolean {
        if (other !is EventOrgItem) return false
        if (format != other.format) return false
        return true
    }

    override fun getLayout(): Int = R.layout.item_format
}