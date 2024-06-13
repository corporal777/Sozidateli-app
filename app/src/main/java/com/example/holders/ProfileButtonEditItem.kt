package com.example.holders

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.example.R
import com.example.databinding.ItemProfileButtonEditBinding
import com.xwray.groupie.databinding.BindableItem
import com.example.extensions.setOnClickListener

class ProfileButtonEditItem(
    private val text: String,
    private val onClickListener: () -> Unit
) : BindableItem<ItemProfileButtonEditBinding>() {

    private var isEditable = true

    override fun bind(viewBinding: ItemProfileButtonEditBinding, position: Int) {
        viewBinding.apply {
            btnEdit.apply {
                isEnabled = isEditable
                text = this@ProfileButtonEditItem.text
                setOnClickListener(onClickListener)
            }
        }
    }


    override fun bind(
        viewBinding: ItemProfileButtonEditBinding,
        position: Int,
        payloads: MutableList<Any>?
    ) {
        val payload = payloads?.firstOrNull()
        if (payload == null) super.bind(viewBinding, position, payloads)
        else {
            if (payload is Boolean) {
                isEditable = payload
                viewBinding.btnEdit.isEnabled = isEditable
            }
        }
    }


    override fun getLayout() = R.layout.item_profile_button_edit
}