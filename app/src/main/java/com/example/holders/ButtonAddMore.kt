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

class ButtonAddMore : Item {

    private val text: String
    private val onClickListener: () -> Unit
    private var isButtonVisible = View.GONE

    constructor(text: String, onClickListener: () -> Unit) : super() {
        this.text = text
        this.onClickListener = onClickListener
    }

    constructor(id: Long, text: String,onClickListener: () -> Unit) : super(id) {
        this.text = text
        this.onClickListener = onClickListener
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            tvHelp.visibility = View.GONE
            btnEdit.apply {
                updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    marginStart = resources.getDimensionPixelSize(R.dimen.profile_data_margin_compact)
                }
                text = this@ButtonAddMore.text
                setOnClickListener(onClickListener)
            }
            btnEdit.visibility = isButtonVisible
            btnEdit.isEnabled = true
            divider.isVisible = false
        }
    }

    fun setButtonVisibility(visibility: Int) {
        isButtonVisible = visibility
        notifyChanged()
    }

    override fun getLayout() = R.layout.item_profile_button_edit
}