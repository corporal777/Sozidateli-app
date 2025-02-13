package com.example.holders

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.app.R
import com.example.app.databinding.ItemHorizontalListBinding
import com.xwray.groupie.viewbinding.BindableItem

open class HorizontalListItem<VH : RecyclerView.ViewHolder>(id: Long) :
    BindableItem<ItemHorizontalListBinding>(id) {

    private var scrollPosition = 0
    private var scrollOffset = 0

    var adapter: RecyclerView.Adapter<VH>? = null

    var backgroundColor = Color.TRANSPARENT
    var isScrollingEnabled = true

    override fun bind(viewBinding: ItemHorizontalListBinding, position: Int) {
        viewBinding.recyclerView.apply {
            adapter = this@HorizontalListItem.adapter
            setBackgroundColor(backgroundColor)
            isNestedScrollingEnabled = isScrollingEnabled
        }
    }

    override fun initializeViewBinding(view: View) = ItemHorizontalListBinding.bind(view)
    override fun getLayout() = R.layout.item_horizontal_list
}