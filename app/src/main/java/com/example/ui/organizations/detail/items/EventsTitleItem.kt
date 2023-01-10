package com.example.ui.organizations.redesign.items

import androidx.core.view.updatePadding
import com.example.R
import com.example.databinding.ItemPartnerBinding
import com.example.databinding.ItemPartnersTitleBinding
import com.example.extensions.dp
import com.xwray.groupie.databinding.BindableItem

class EventsTitleItem(
    val title: String,
    val pTop: Int = 0,
    val pBottom: Int = 0,
    val pLeft: Int = 0,
    val pRight: Int = 0,
) : BindableItem<ItemPartnersTitleBinding>() {

    override fun bind(viewBinding: ItemPartnersTitleBinding, position: Int) {
        viewBinding.tvTitle.apply {
            text = title
            if (pTop != 0) {
                updatePadding(top = pTop.dp)
            }
            if (pBottom != 0) {
                updatePadding(bottom = pBottom.dp)
            }
            if (pLeft != 0) {
                updatePadding(left = pLeft.dp)
            }
            if (pRight != 0) {
                updatePadding(right = pRight.dp)
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_partners_title
}