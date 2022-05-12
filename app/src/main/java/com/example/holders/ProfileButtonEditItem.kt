package com.example.holders

import android.view.View
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
    private var isHelpVis: Boolean = false
    private var isEditable = true

    var hasDivider = true
    var compactMargin = false

    constructor(text: String, isHelpVis: Boolean, onClickListener: () -> Unit) : super() {
        this.text = text
        this.onClickListener = onClickListener
        this.isHelpVis = isHelpVis
    }

    constructor(id: Long, text: String, isHelpVis: Boolean, onClickListener: () -> Unit) : super(id) {
        this.text = text
        this.onClickListener = onClickListener
        this.isHelpVis = isHelpVis
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            tvHelp.visibility = if (isHelpVis) View.VISIBLE else View.GONE
            btnEdit.apply {
                updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    marginStart = resources.getDimensionPixelSize(if (compactMargin) R.dimen.profile_data_margin_compact
                    else R.dimen.profile_data_margin)
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

    override fun getLayout() = R.layout.item_profile_button_edit
}