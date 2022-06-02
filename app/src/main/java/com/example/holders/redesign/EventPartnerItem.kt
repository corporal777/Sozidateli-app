package com.example.holders.redesign

import com.example.R
import com.example.databinding.ItemPartnerBinding
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
//            if (!title.isNullOrEmpty()){
//                if (title.length > 83){
//                    tvPartnerTitle.text = StringBuilder(title.substring(0,60)).append("...")
//                }
//                else tvPartnerTitle.text = title
//            }else {
//                tvPartnerTitle.text = ""
//            }

            tvPartnerTitle.text = title
            ivPartnerImage.setImage(image)

            root.setOnClickListener {
               onPartnerClick(id)
            }

        }
    }

    override fun getLayout(): Int = R.layout.item_partner
}