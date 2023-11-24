package com.example.holders.redesign

import com.example.R
import com.example.databinding.ItemPartnerBinding
import com.example.extensions.markWon
import com.example.util.setImage
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem

class EventPartnerItem(
    val id: Int?,
    val name: String?,
    val title: String?,
    val image: String?,
    val onPartnerClick: (id: Int) -> Unit
) : BindableItem<ItemPartnerBinding>(id?.toLong() ?: 0) {


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
                if (id != null) onPartnerClick(id)
            }
        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is EventPartnerItem) return false
        if (id != other.id) return false
        if (name != other.name) return false
        if (title != other.title) return false
        if (image != other.image) return false
        return true
    }

    override fun getLayout(): Int = R.layout.item_partner
}