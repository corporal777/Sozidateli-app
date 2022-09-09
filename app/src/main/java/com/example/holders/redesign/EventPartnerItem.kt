package com.example.holders.redesign

import com.example.R
import com.example.databinding.ItemPartnerBinding
import com.example.util.markWon
import com.example.util.setImage
import com.xwray.groupie.databinding.BindableItem

class EventPartnerItem(
    val id: Int,
    val name: String?,
    val title: String?,
    val image: String?,
    val onPartnerClick: (id : Int) -> Unit
) : BindableItem<ItemPartnerBinding>() {


    override fun bind(viewBinding: ItemPartnerBinding, position: Int) {
        viewBinding.apply {

            tvPartnerName.text = name ?: ""
            markWon(viewBinding.root.context).setMarkdown(
                tvPartnerTitle,
                title ?: ""
            )
            //tvPartnerTitle.text = title
            ivPartnerImage.setImage(image)

            root.setOnClickListener {
               onPartnerClick(id)
            }

        }
    }

    override fun getLayout(): Int = R.layout.item_partner
}