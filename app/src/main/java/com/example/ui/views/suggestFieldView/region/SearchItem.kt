package com.example.ui.views.suggestFieldView.region

import android.view.View
import com.example.app.R
import com.example.app.databinding.ItemFormatBinding
import com.xwray.groupie.Item
import com.xwray.groupie.viewbinding.BindableItem

class SearchItem(
    private val itemId: Int?,
    private val region: String?,
    private val onRegionClick: (region: String) -> Unit
) : BindableItem<ItemFormatBinding>(itemId?.toLong() ?: 0) {


    override fun bind(viewBinding: ItemFormatBinding, position: Int) {
        viewBinding.apply {
            tvContent.text = region
            root.setOnClickListener {
                onRegionClick.invoke(region ?: "")
            }
        }
    }

    override fun hasSameContentAs(other: Item<*>): Boolean {
        if (other !is SearchItem) return false
        if (region != other.region) return false
        return true
    }

    override fun initializeViewBinding(view: View) = ItemFormatBinding.bind(view)
    override fun getLayout(): Int = R.layout.item_format
}