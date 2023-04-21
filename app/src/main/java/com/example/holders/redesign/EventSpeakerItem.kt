package com.example.holders.redesign

import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import com.example.R
import com.example.databinding.ItemSpeakerNewBinding
import com.example.ui.event.about.items.EventDetailActionItem
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
) : BindableItem<ItemSpeakerNewBinding>(id.toLong()) {


    override fun bind(viewBinding: ItemSpeakerNewBinding, position: Int) {
        viewBinding.apply {
            ivSpeakerImage.setImage(image, error = R.drawable.empty_speaker_avatar)
            tvSpeakersName.text = name
            root.setOnClickListener {
                onItemClick(id)
            }
            decorSpeakerStatus(status, ivSpeakerStatus)
        }
    }


    private fun decorSpeakerStatus(status: String, imageView: ImageView) {
        var mIcon = 0
        var mText = ""
        var visibility = false
        if (isRegistered) {
            when (status) {
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
                else -> visibility = false
            }
        } else {
            visibility = true
            mIcon = R.drawable.ic_speaker_status_not_confirmed
            mText = "Спикер еще не зарегистрирован в «Созидателях»"
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
        if (other !is EventSpeakerItem) return false
        if (id != other.id) return false
        if (name != other.name) return false
        if (image != other.image) return false
        if (status != other.status) return false
        if (isRegistered != other.isRegistered) return false
        return true
    }


    override fun getLayout(): Int = R.layout.item_speaker_new
}