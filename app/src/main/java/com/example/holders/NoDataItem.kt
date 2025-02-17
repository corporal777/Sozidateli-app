package com.example.holders

import android.view.View
import androidx.core.view.isVisible
import com.example.app.R
import com.example.app.databinding.ItemNoDataBinding
import com.xwray.groupie.viewbinding.BindableItem

class NoDataItem(
    private val title: String,
    private val description: String? = null,
    private val padding: Int = 0
) : BindableItem<ItemNoDataBinding>(-1000) {

    override fun bind(viewBinding: ItemNoDataBinding, position: Int) {
        viewBinding.apply {
            tvTitle.text = title
            tvDescription.apply {
                text = description
                isVisible = !description.isNullOrEmpty()
            }
        }
    }

    override fun initializeViewBinding(view: View) = ItemNoDataBinding.bind(view)
    override fun getLayout() = R.layout.item_no_data
}