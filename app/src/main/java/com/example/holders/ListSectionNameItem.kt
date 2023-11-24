package com.example.holders

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.example.R
import com.example.databinding.ItemListSectionNameBinding
import com.example.extensions.dp
import com.xwray.groupie.databinding.BindableItem

class ListSectionNameItem(
    id: Long,
    private val name: String? = null
) : BindableItem<ItemListSectionNameBinding>(id) {

    var withTopMargin = false

    override fun bind(viewBinding: ItemListSectionNameBinding, position: Int) {
        viewBinding.tvName.apply {
            text = name
            isVisible = !name.isNullOrEmpty()
            updateLayoutParams<ViewGroup.MarginLayoutParams> {
                this.topMargin = if (withTopMargin) 20.dp else 0
            }
        }
    }


    override fun getLayout() = R.layout.item_list_section_name
}