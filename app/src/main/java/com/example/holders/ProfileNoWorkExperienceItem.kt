package com.example.holders

import android.view.View
import com.example.app.R
import com.example.app.databinding.ItemNoWorkDataBinding
import com.xwray.groupie.viewbinding.BindableItem

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

    override fun initializeViewBinding(view: View) = ItemNoWorkDataBinding.bind(view)
    override fun getLayout() = R.layout.item_no_work_data
}