package com.example.holders.redesign

import android.view.View
import androidx.core.view.isVisible
import com.example.app.R
import com.example.app.databinding.ItemEventSpeakerBinding
import com.example.ui.views.dialogs.DefaultAlertDialog
import com.example.util.setImage
import com.xwray.groupie.viewbinding.BindableItem

class EventSpeakerItem(
    private val speakerId: Int?,
    private val name: String?,
    private val image: String?,
    private val status: String?,
    private val onItemClick: (id: Int) -> Unit
) : BindableItem<ItemEventSpeakerBinding>(speakerId?.toLong() ?: 0) {


    override fun bind(viewBinding: ItemEventSpeakerBinding, position: Int) {
        viewBinding.apply {
            ivSpeakerImage.apply {
                setImage(
                    image = image,
                    error = R.drawable.empty_speaker_avatar,
                    placeholder = R.drawable.background_image_placeholder
                )
            }
            tvSpeakersName.text = name
            ivSpeakerStatus.apply {
                var message = ""
                when (status) {
                    "not_registered" -> {
                        isVisible = true
                        setImageResource(R.drawable.ic_speaker_status_not_confirmed)
                        message = "Спикер еще не зарегистрирован в «Созидателях»"
                    }

                    "pending" -> {
                        isVisible = true
                        setImageResource(R.drawable.ic_speaker_status_pending)
                        message =
                            "Спикер еще не подтвердил свое участие в мероприятии с помощью профиля в «Созидателях»"
                    }

                    "approved" -> {
                        isVisible = true
                        setImageResource(R.drawable.ic_speaker_status_confirmed)
                        message =
                            "Спикер подтвердил свое участие в мероприятии с помощью профиля в «Созидателях»"
                    }

                    else -> isVisible = false
                }
                setOnClickListener { DefaultAlertDialog(context, null, message) }
            }
            itemContainer.apply {
                clipToOutline = true
                setOnClickListener { onItemClick(speakerId ?: 0) }
            }
        }
    }


    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (other !is EventSpeakerItem) return false
        if (id != other.id) return false
        if (name != other.name) return false
        if (image != other.image) return false
        if (status != other.status) return false
        return true
    }

    override fun initializeViewBinding(view: View) = ItemEventSpeakerBinding.bind(view)
    override fun getLayout(): Int = R.layout.item_event_speaker
}