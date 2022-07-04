package com.example.ui.event.speakers.member.items

import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.UserDetail
import com.example.databinding.ItemUserSpeakerMainInfoBinding
import com.example.ui.views.dialogs_new.MessageDialogWithBrownButton
import com.example.util.setImage
import com.xwray.groupie.databinding.BindableItem

class UserSpeakerMainInfoItem(
    private val isCurrentUser: Boolean,
    private val speaker: UserDetail?,
    private val name: String,
    private val location: String?,
    private val description: String?,
    private val avatar: String?,
    val status: String,
    val isRegistered: Boolean,
    private val onWriteMessageClick: (speaker: UserDetail) -> Unit
) : BindableItem<ItemUserSpeakerMainInfoBinding>() {


    override fun bind(viewBinding: ItemUserSpeakerMainInfoBinding, position: Int) {
        viewBinding.apply {

            if (isRegistered) {
                btnWriteMessage.isVisible = !isCurrentUser
            } else btnWriteMessage.isVisible = false


            ivSpeakerImage.apply {
                setImage(avatar, error = R.drawable.empty_speaker_avatar)
            }
            tvSpeakersName.text = name
            tvSpeakersLocation.apply {
                isVisible = !location.isNullOrEmpty()
                text = location
            }

            tvSpeakersPosition.text = description ?: "No description"

            btnWriteMessage.setOnClickListener {
                if (speaker != null) {
                    onWriteMessageClick(speaker)
                }
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

    override fun getLayout(): Int = R.layout.item_user_speaker_main_info
}