package com.example.holders

import android.view.View
import com.example.app.R
import com.example.app.databinding.ItemEmptyBinding
import com.xwray.groupie.viewbinding.BindableItem

class EmptyItem(private val text: String) : BindableItem<ItemEmptyBinding>(-1L) {

    override fun bind(viewBinding: ItemEmptyBinding, position: Int) {
        viewBinding.tvNoChats.text = text
    }


    override fun initializeViewBinding(view: View) = ItemEmptyBinding.bind(view)
    override fun getLayout() = R.layout.item_empty

}