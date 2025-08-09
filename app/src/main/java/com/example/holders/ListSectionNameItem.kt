package com.example.holders

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.example.app.R
import com.example.app.databinding.ItemListSectionNameBinding
import com.example.common.dp
import com.xwray.groupie.viewbinding.BindableItem

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

    override fun initializeViewBinding(view: View) = ItemListSectionNameBinding.bind(view)
    override fun getLayout() = R.layout.item_list_section_name
}