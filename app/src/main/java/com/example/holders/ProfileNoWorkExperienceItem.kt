package com.example.holders

import com.example.R
import com.example.databinding.ItemNoWorkDataBinding
import com.xwray.groupie.databinding.BindableItem

class ProfileNoWorkExperienceItem(
    private val message: String
) : BindableItem<ItemNoWorkDataBinding>() {

    override fun bind(viewBinding: ItemNoWorkDataBinding, position: Int) {
        viewBinding.apply {
            tvText.apply {
                text = message
            }
        }
    }


    override fun getLayout() = R.layout.item_no_work_data
}