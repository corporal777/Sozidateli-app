package com.example.ui.organizations.redesign.items

import androidx.databinding.Bindable
import com.example.R
import com.example.databinding.ItemProfileButtonEditNewBinding
import com.xwray.groupie.databinding.BindableItem

class ShowButtonItem(val btnText: String, val clickListener: () -> Unit) :
    BindableItem<ItemProfileButtonEditNewBinding>() {


    override fun bind(viewBinding: ItemProfileButtonEditNewBinding, position: Int) {
        viewBinding.btnEdit.apply {
            text = btnText
            setOnClickListener {
                clickListener.invoke()
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_profile_button_edit_new
}