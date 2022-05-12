package com.example.holders.redesign

import com.example.R
import com.example.databinding.ItemSpeakerNewBinding
import com.example.util.setImage
import com.xwray.groupie.databinding.BindableItem

class EventSpeakerItem(
    val id: Int,
    val name: String?,
    val image: String,
    val onItemClick:(id : Int)-> Unit
) : BindableItem<ItemSpeakerNewBinding>() {


    override fun bind(viewBinding: ItemSpeakerNewBinding, position: Int) {
        viewBinding.apply {

            ivSpeakerImage.apply {
                setImage(image)
            }
            tvSpeakersName.text = name

            root.setOnClickListener {
                onItemClick(id)
            }

        }
    }

    override fun getLayout(): Int = R.layout.item_speaker_new
}