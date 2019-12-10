package com.example.holders

import com.example.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_speaker_description.*

class SpeakerDescriptionItem(id: Long, private val description: String) : Item(id) {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.tvDescription.text = description
    }

    override fun getLayout() = R.layout.item_speaker_description

}