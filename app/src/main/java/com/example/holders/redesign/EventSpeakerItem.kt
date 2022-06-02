package com.example.holders.redesign

import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import com.example.R
import com.example.databinding.ItemSpeakerNewBinding
import com.example.ui.views.dialogs_new.MessageDialogWithBrownButton
import com.example.util.setImage
import com.xwray.groupie.databinding.BindableItem

class EventSpeakerItem(
    val id: Int,
    val name: String?,
    val image: String,
    val status: String,
    val isRegistered: Boolean,
    val onItemClick: (id: Int) -> Unit
) : BindableItem<ItemSpeakerNewBinding>() {


    override fun bind(viewBinding: ItemSpeakerNewBinding, position: Int) {
        viewBinding.apply {

            ivSpeakerImage.apply {
                setImage(image, error = R.drawable.empty_speaker_avatar)
            }
            tvSpeakersName.text = name

            root.setOnClickListener {
                onItemClick(id)
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


    override fun getLayout(): Int = R.layout.item_speaker_new
}