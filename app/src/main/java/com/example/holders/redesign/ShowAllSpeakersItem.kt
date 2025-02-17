package com.example.holders.redesign

import android.view.View
import com.example.app.R
import com.example.app.databinding.ItemShowSpeakersBinding
import com.xwray.groupie.viewbinding.BindableItem

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

    override fun initializeViewBinding(view: View) = ItemShowSpeakersBinding.bind(view)
    override fun getLayout(): Int = R.layout.item_show_speakers
}