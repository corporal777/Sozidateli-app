package com.example.holders

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.example.R
import com.example.databinding.ItemProfileButtonEditBinding
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_profile_button_edit.*

class ProfileDataFileAddItem(
    private val isButtonEditable : Boolean,
    private val onClickListener: () -> Unit
) : BindableItem<ItemProfileButtonEditBinding>(-1001L) {

    private var isEditable = isButtonEditable

    override fun bind(viewBinding: ItemProfileButtonEditBinding, position: Int) {
        viewBinding.apply {
            tvHelp.visibility = View.VISIBLE
            btnEdit.apply {
                updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    marginStart = resources.getDimensionPixelSize(R.dimen.profile_data_margin_compact)
                }
                text = context.getString(R.string.add_file)
                isEnabled = isEditable
                setOnClickListener {
                    onClickListener.invoke()
                }
            }
            divider.isVisible = false
        }
    }


    fun updateButton(payload: Boolean){
        isEditable = payload
    }


    override fun getLayout() = R.layout.item_profile_button_edit
}