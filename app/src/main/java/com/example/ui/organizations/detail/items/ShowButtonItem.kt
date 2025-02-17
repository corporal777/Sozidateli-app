package com.example.ui.organizations.detail.items

import android.view.View
import com.example.app.R
import com.example.app.databinding.ItemProfileButtonEditNewBinding
import com.xwray.groupie.viewbinding.BindableItem

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

    override fun initializeViewBinding(view: View) = ItemProfileButtonEditNewBinding.bind(view)
    override fun getLayout(): Int = R.layout.item_profile_button_edit_new
}