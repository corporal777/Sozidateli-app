package com.example.ui.subevent.items

import android.view.View
import androidx.core.view.isVisible
import com.example.R
import com.example.databinding.ItemSubEventSpeakerBinding
import com.example.util.setImage
import com.xwray.groupie.databinding.BindableItem
import kotlinx.android.synthetic.main.item_lecture.*

class SubEventSpeakerItem(
        private val id: Int,
        private val name: String,
        private val location: String,
        private val description: String?,
        private val avatar: String?,
        private val onSpeakerClick: () -> Unit
) : BindableItem<ItemSubEventSpeakerBinding>() {


    override fun bind(viewBinding: ItemSubEventSpeakerBinding, position: Int) {
        viewBinding.apply {

            ivSpeakerImage.apply {
                setImage(avatar)
            }
            tvSpeakersName.text = name
            tvSpeakersLocation.apply {
                isVisible = !location.isNullOrEmpty()
                text = location
            }

            val fullDescription = description
            if (!fullDescription.isNullOrEmpty()) {
                if (fullDescription.length > 115) {
                    val shortDescription = StringBuilder(
                        fullDescription.substring(0, 114).replace("\n", " ")
                    ).append("...")
                        .toString()
                    tvSpeakersPosition.text = shortDescription

                } else {
                    tvSpeakersPosition.text = fullDescription
                }
            }

            //tvSpeakersPosition.text = description

            root.setOnClickListener {
                onSpeakerClick.invoke()
            }

        }
    }

    override fun getLayout(): Int = R.layout.item_sub_event_speaker
}