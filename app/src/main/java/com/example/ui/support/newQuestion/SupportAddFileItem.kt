package com.example.ui.support.newQuestion

import com.example.R
import com.example.databinding.ItemSupportAddFileBinding
import com.xwray.groupie.databinding.BindableItem

class SupportAddFileItem (
    val addFileClick: () -> Unit,
) : BindableItem<ItemSupportAddFileBinding>(-1001L) {


    override fun bind(viewBinding: ItemSupportAddFileBinding, position: Int) {
        viewBinding.apply {
            cvImage.setOnClickListener {
                addFileClick.invoke()
            }
        }
    }



    override fun getLayout(): Int = R.layout.item_support_add_file
}