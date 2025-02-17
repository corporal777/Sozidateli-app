package com.example.ui.views.suggestFieldView.region

import android.view.View
import com.example.app.R
import com.example.app.databinding.ItemSearchEmptyBinding
import com.xwray.groupie.Item
import com.xwray.groupie.viewbinding.BindableItem

class SearchEmptyItem (
    private val title: String,
) : BindableItem<ItemSearchEmptyBinding>(-1001L) {


    override fun bind(viewBinding: ItemSearchEmptyBinding, position: Int) {
        viewBinding.apply {
            tvTitle.text = title
        }
    }

    override fun hasSameContentAs(other: Item<*>): Boolean {
        if (other !is SearchEmptyItem) return false
        return true
    }

    override fun initializeViewBinding(view: View) = ItemSearchEmptyBinding.bind(view)
    override fun getLayout(): Int = R.layout.item_search_empty
}