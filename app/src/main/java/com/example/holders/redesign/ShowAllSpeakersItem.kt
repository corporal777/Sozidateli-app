package com.example.holders.redesign

import androidx.databinding.Bindable
import com.example.R
import com.example.databinding.ItemShowSpeakersBinding
import com.xwray.groupie.databinding.BindableItem

class ShowAllSpeakersItem(
    val showAllSpeakers: () -> Unit
) : BindableItem<ItemShowSpeakersBinding>() {

    override fun bind(viewBinding: ItemShowSpeakersBinding, position: Int) {
        viewBinding.apply {
            root.setOnClickListener {
              showAllSpeakers.invoke()
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_show_speakers
}