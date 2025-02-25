package com.example.holders

import android.view.View
import android.view.animation.AlphaAnimation
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import com.example.app.R
import com.example.app.databinding.ItemNoEventBinding
import com.example.extensions.dp
import com.xwray.groupie.viewbinding.BindableItem

class NoEventItem(
    val title: String,
    val description: String = "",
    val paddingTop: Int = 0,
    val paddingBottom: Int = 0
) : BindableItem<ItemNoEventBinding>() {

    override fun bind(viewBinding: ItemNoEventBinding, position: Int) {
        viewBinding.apply {
            noDataPlaceholder.updatePadding(top = paddingTop.dp, bottom = paddingBottom.dp)
            tvTitle.text = title
            if (description.isNullOrEmpty()) {
                tvDescription.isVisible = false
            } else {
                tvDescription.text = description
            }
            setFadeAnimation(this.root)
        }
    }

    private fun setFadeAnimation(view: View) {
        val anim = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 350
        view.startAnimation(anim)
    }

    override fun initializeViewBinding(view: View) = ItemNoEventBinding.bind(view)
    override fun getLayout(): Int = R.layout.item_no_event
}