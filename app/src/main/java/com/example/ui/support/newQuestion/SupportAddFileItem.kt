package com.example.ui.support.newQuestion

import android.view.View
import com.example.app.R
import com.example.app.databinding.ItemSupportAddFileBinding
import com.xwray.groupie.viewbinding.BindableItem

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


    override fun initializeViewBinding(view: View) = ItemSupportAddFileBinding.bind(view)
    override fun getLayout(): Int = R.layout.item_support_add_file
}