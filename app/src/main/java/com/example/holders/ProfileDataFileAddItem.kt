package com.example.holders

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.example.R
import com.example.databinding.ItemProfileAttachFileBinding
import com.example.databinding.ItemProfileButtonEditBinding
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_profile_button_edit.*

class ProfileDataFileAddItem(
    private val isButtonEditable : Boolean,
    private val onClickListener: () -> Unit
) : BindableItem<ItemProfileAttachFileBinding>(-1001L) {

    private var isEditable = isButtonEditable

    override fun bind(viewBinding: ItemProfileAttachFileBinding, position: Int) {
        viewBinding.apply {
            tvEdit.apply {
                isEnabled = isEditable
                setOnClickListener {
                    onClickListener.invoke()
                }
            }
        }
    }


    fun updateButton(payload: Boolean){
        isEditable = payload
    }


    override fun getLayout() = R.layout.item_profile_attach_file
}