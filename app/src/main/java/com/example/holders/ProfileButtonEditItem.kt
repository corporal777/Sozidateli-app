package com.example.holders

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.example.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_profile_button_edit.*
import setOnClickListener

class ProfileButtonEditItem : Item {

    private val text: String
    private val onClickListener: () -> Unit

    var hasDivider = true
    var compactMargin = false

    constructor(text: String, onClickListener: () -> Unit) : super() {
        this.text = text
        this.onClickListener = onClickListener
    }

    constructor(id: Long, text: String, onClickListener: () -> Unit) : super(id) {
        this.text = text
        this.onClickListener = onClickListener
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            btnEdit.apply {
                updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    marginStart = resources.getDimensionPixelSize(if (compactMargin) R.dimen.profile_data_margin_compact
                    else R.dimen.profile_data_margin)
                }
                text = this@ProfileButtonEditItem.text
                setOnClickListener(onClickListener)
            }

            divider.isVisible = hasDivider
        }
    }

    override fun getLayout() = R.layout.item_profile_button_edit
}