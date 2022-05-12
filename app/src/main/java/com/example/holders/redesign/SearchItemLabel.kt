package com.example.holders.redesign

import android.view.View
import com.example.R
import com.example.databinding.ItemTitleBinding
import com.xwray.groupie.databinding.BindableItem

class SearchItemLabel(
    var title: String
): BindableItem<ItemTitleBinding>() {

    override fun bind(viewBinding: ItemTitleBinding, position: Int) {
        if (title.isNullOrEmpty()){
            viewBinding.titleContainer.visibility = View.GONE
        }else {
            viewBinding.titleContainer.visibility = View.VISIBLE
            viewBinding.tvTitle.text = title
        }

    }

    override fun getLayout(): Int = R.layout.item_title
}