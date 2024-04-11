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
    private var isHelpVis: Boolean,
    private val onClickListener: () -> Unit
) : BindableItem<ItemProfileButtonEditBinding>() {

    private var isEditable = true

    var hasDivider = true
    var compactMargin = false

    override fun bind(viewBinding: ItemProfileButtonEditBinding, position: Int) {
        viewBinding.apply {
            tvHelp.visibility = if (isHelpVis) View.VISIBLE else View.GONE
            btnEdit.apply {
                updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    marginStart = resources.getDimensionPixelSize(
                        if (compactMargin) R.dimen.profile_data_margin_compact
                        else R.dimen.profile_data_margin
                    )
                }
                text = this@ProfileButtonEditItem.text
                setOnClickListener(onClickListener)
            }
            btnEdit.isEnabled = isEditable
            divider.isVisible = hasDivider
        }
    }


    fun hasExp(hasExp: Boolean) {
        isEditable = !hasExp
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