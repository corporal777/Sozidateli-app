package com.example.ui.event.registration.items

import android.view.View
import android.widget.TextView
import com.example.app.R
import com.example.app.databinding.ItemRegisterEventProfileMainBinding
import com.example.holders.registerEvent.BaseRegisterProfileItem

class REProfileStringItem(
    val name: String,
    val field: String?,
    val onClick: () -> Unit
) : BaseRegisterProfileItem<ItemRegisterEventProfileMainBinding>(name.hashCode().toString().toLong()) {


    override fun bind(viewBinding: ItemRegisterEventProfileMainBinding, position: Int) {
        super.bind(viewBinding, position)
        viewBinding.apply {
            prefilledFieldTitle.text = name
            prefilledFieldTextView.text = field
            btnAction.setOnClickListener { onClick.invoke() }
        }
    }


    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (other !is REProfileStringItem) return false
        if (name != other.name) return false
        if (field != other.field) return false
        return true
    }

    override fun getTitleView(binding: ItemRegisterEventProfileMainBinding): TextView = binding.prefilledFieldTitle
    override fun getErrorFrameView(binding: ItemRegisterEventProfileMainBinding): View = binding.viewInputError

    override fun getLayout(): Int = R.layout.item_register_event_profile_main
    override fun initializeViewBinding(view: View) = ItemRegisterEventProfileMainBinding.bind(view)
}