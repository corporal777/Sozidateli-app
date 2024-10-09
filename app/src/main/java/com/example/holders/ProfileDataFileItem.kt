package com.example.holders

import com.example.app.R
import com.example.app.databinding.ItemFileNameBinding
import com.xwray.groupie.databinding.BindableItem

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

    override fun getLayout() = R.layout.item_file_name
}