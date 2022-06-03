package com.example.ui.subevent.items

import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import com.example.R
import com.example.databinding.ItemSubEventSpeakerBinding
import com.example.ui.views.dialogs_new.MessageDialogWithBrownButton
import com.example.util.setImage
import com.xwray.groupie.databinding.BindableItem

class SubEventSpeakerItem(
        private val id: Int,
        private val name: String,
        private val location: String,
        private val description: String?,
        private val avatar: String?,
        val status : String,
        val isRegistered: Boolean,
        private val onSpeakerClick: () -> Unit
) : BindableItem<ItemSubEventSpeakerBinding>() {


    override fun bind(viewBinding: ItemSubEventSpeakerBinding, position: Int) {
        viewBinding.apply {

            ivSpeakerImage.apply {
                setImage(avatar, error = R.drawable.empty_speaker_avatar)
            }
            tvSpeakersName.text = name
            tvSpeakersLocation.apply {
                isVisible = !location.isNullOrEmpty()
                text = location
            }
//            val fullDescription = description
//            if (!fullDescription.isNullOrEmpty()) {
//                if (fullDescription.length > 140) {
//                    val shortDescription = StringBuilder(
//                        fullDescription.substring(0, 139).replace("\n", " ")
//                    ).append("...")
//                        .toString()
//                    tvSpeakersPosition.text = shortDescription
//
//                } else {
//                    tvSpeakersPosition.text = fullDescription
//                }
//            }

            tvSpeakersPosition.text = description

            root.setOnClickListener {
                onSpeakerClick.invoke()
            }

            decorSpeakerStatus(status, ivSpeakerStatus, btnShowSpeakerStatus)
        }
    }

    private fun decorSpeakerStatus(status: String, imageView: ImageView, btn: ViewGroup) {
        var mIcon = 0
        var mText = ""
        var visibility = false
        if (isRegistered) {
            when (status) {
                "pending" -> {
                    if (isRegistered) {
                        visibility = true
                        mIcon = R.drawable.ic_speaker_status_pending
                        mText =
                            "Спикер еще не подтвердил свое участие в мероприятии с помощью профиля в «Созидателях»"
                    }
                }
                "approved" -> {
                    visibility = true
                    mIcon = R.drawable.ic_speaker_status_confirmed
                    mText =
                        "Спикер подтвердил свое участие в мероприятии с помощью профиля в «Созидателях»"
                }
                else -> visibility = false
            }
        } else {
            visibility = true
            mIcon = R.drawable.ic_speaker_status_not_confirmed
            mText = "Спикер еще не зарегистрирован в «Созидателях»"
        }

        imageView.setImageResource(mIcon)
        btn.apply {
            isVisible = visibility
            setOnClickListener {
                MessageDialogWithBrownButton(btn.context, mText)
            }
        }

    }

    override fun getLayout(): Int = R.layout.item_sub_event_speaker
}