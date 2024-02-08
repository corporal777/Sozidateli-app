package com.example.ui.event.speakers.member.items

import android.widget.ImageView
import androidx.core.view.isVisible
import com.example.R
import com.example.databinding.ItemUserSpeakerMainInfoBinding
import com.example.extensions.markWon
import com.example.ui.views.dialogs.MessageDialogWithBrownButton
import com.example.util.setImage
import com.xwray.groupie.databinding.BindableItem

class UserSpeakerMainInfoItem(
    private val id: Int?,
    private val isCurrentUser: Boolean,
    private val name: String?,
    private val location: String?,
    private val description: String?,
    private val avatar: String?,
    val status: String?,
    val isRegistered: Boolean?,
    private val onWriteMessageClick: () -> Unit
) : BindableItem<ItemUserSpeakerMainInfoBinding>(id?.toLong() ?: 0) {


    override fun bind(viewBinding: ItemUserSpeakerMainInfoBinding, position: Int) {
        viewBinding.apply {
            ivSpeakerImage.apply {
                clipToOutline = true
                setImage(avatar, error = R.drawable.empty_speaker_avatar)
            }
            tvSpeakersName.text = name
            tvSpeakersLocation.apply {
                isVisible = !location.isNullOrEmpty()
                text = location
            }
            tvSpeakersPosition.apply {
                isVisible = !description.isNullOrEmpty()
                markWon(context).setMarkdown(this, description ?: "")
            }
            btnWriteMessage.apply {
                isVisible = !isCurrentUser && isRegistered == true
                setOnClickListener {
                    onWriteMessageClick()
                }
            }
            decorSpeakerStatus(status, ivSpeakerStatus)
        }
    }

    private fun decorSpeakerStatus(status: String?, imageView: ImageView) {
        var mIcon = R.drawable.ic_speaker_status_not_confirmed
        var mText = ""
        var visibility = false
        when (status) {
            "not_registered" -> {
                visibility = true
                mIcon = R.drawable.ic_speaker_status_not_confirmed
                mText = "Спикер еще не зарегистрирован в «Созидателях»"
            }
            "pending" -> {
                visibility = true
                mIcon = R.drawable.ic_speaker_status_pending
                mText = "Спикер еще не подтвердил свое участие в мероприятии с помощью профиля в «Созидателях»"
            }
            "approved" -> {
                visibility = true
                mIcon = R.drawable.ic_speaker_status_confirmed
                mText = "Спикер подтвердил свое участие в мероприятии с помощью профиля в «Созидателях»"
            }
        }

        imageView.apply {
            isVisible = visibility
            setImageResource(mIcon)
            setOnClickListener {
                MessageDialogWithBrownButton(context, mText)
            }
        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is UserSpeakerMainInfoItem) return false
        if (isCurrentUser != other.isCurrentUser) return false
        if (name != other.name) return false
        if (location != other.location) return false
        if (description != other.description) return false
        if (avatar != other.avatar) return false
        if (status != other.status) return false
        if (isRegistered != other.isRegistered) return false
        return true
    }

    override fun getLayout(): Int = R.layout.item_user_speaker_main_info
}