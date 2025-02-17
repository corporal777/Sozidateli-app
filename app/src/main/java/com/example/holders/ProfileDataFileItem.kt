package com.example.holders

import android.view.View
import com.example.app.R
import com.example.app.databinding.ItemFileNameBinding
import com.xwray.groupie.viewbinding.BindableItem

class ProfileDataFileItem(
    private val name: String,
    private val onFileClick: () -> Unit
) : BindableItem<ItemFileNameBinding>() {

    override fun bind(viewBinding: ItemFileNameBinding, position: Int) {
        viewBinding.apply {
            tvFileName.apply {
                text = name
                isClickable = false
            }
            tvFileName.setOnClickListener { onFileClick() }
        }
    }

    override fun initializeViewBinding(view: View) = ItemFileNameBinding.bind(view)
    override fun getLayout() = R.layout.item_file_name
}