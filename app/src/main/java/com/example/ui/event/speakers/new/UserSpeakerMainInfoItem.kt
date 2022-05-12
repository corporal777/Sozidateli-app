package com.example.ui.event.speakers.new

import com.example.R
import com.example.data.models.UserDetail
import com.example.databinding.ItemSubEventSpeakerBinding
import com.example.databinding.ItemUserSpeakerMainInfoBinding
import com.example.util.setImage
import com.xwray.groupie.databinding.BindableItem

class UserSpeakerMainInfoItem(
    private val speaker: UserDetail,
    private val name: String,
    private val location: String?,
    private val description: String?,
    private val avatar: String?,
    private val onWriteMessageClick: (speaker : UserDetail) -> Unit
) : BindableItem<ItemUserSpeakerMainInfoBinding>() {


    override fun bind(viewBinding: ItemUserSpeakerMainInfoBinding, position: Int) {
        viewBinding.apply {

            ivSpeakerImage.apply {
                setImage(avatar)
            }
            tvSpeakersName.text = name
            tvSpeakersLocation.text = location ?: "Москва"
            tvSpeakersPosition.text = description ?: "Доцент МГУ имени М.В.Ломоносова, Факультет журналистики, Кафедра литературно-художественной критики и публицистики, Москва, Россия (2021)"

            btnWriteMessage.setOnClickListener {
                onWriteMessageClick(speaker)
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_user_speaker_main_info
}