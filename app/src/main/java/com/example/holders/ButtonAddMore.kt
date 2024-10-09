package com.example.holders

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.example.app.R
import com.example.app.databinding.ItemProfileButtonEditBinding
import com.xwray.groupie.databinding.BindableItem
import com.example.extensions.setOnClickListener

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

    override fun getLayout() = R.layout.item_profile_button_edit
}