package com.example.holders

import android.view.View
import androidx.core.view.isVisible
import com.example.app.R
import com.example.app.databinding.ItemProfileButtonEditBinding
import com.example.extensions.setOnClickListener
import com.xwray.groupie.viewbinding.BindableItem

class ButtonAddMore(
    private val text: String,
    private val onClickListener: () -> Unit
) : BindableItem<ItemProfileButtonEditBinding>() {

    private var isButtonVisible = View.GONE
    private var isButtonEnabled = true

    override fun bind(viewBinding: ItemProfileButtonEditBinding, position: Int) {
        viewBinding.apply {
            tvHelp.visibility = View.GONE
            btnEdit.apply {
                text = this@ButtonAddMore.text
                setOnClickListener(onClickListener)
            }
            btnEdit.visibility = isButtonVisible
            btnEdit.isEnabled = isButtonEnabled
            divider.isVisible = false
        }
    }

    fun setButtonVisibility(visibility: Int) {
        isButtonVisible = visibility
        notifyChanged()
    }

    fun setButtonEnabled(enabled: Boolean) {
        isButtonEnabled = enabled
        notifyChanged()
    }

    override fun initializeViewBinding(view: View) = ItemProfileButtonEditBinding.bind(view)
    override fun getLayout() = R.layout.item_profile_button_edit
}