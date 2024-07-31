package com.example.ui.subevent.items

import android.content.Context
import android.widget.ImageView
import androidx.core.view.isVisible
import com.example.R
import com.example.databinding.ItemSubEventSpeakerBinding
import com.example.extensions.markWon
import com.example.ui.views.dialogs.DefaultAlertDialog
import com.example.util.MarkdownEmphasisPlugin
import com.example.util.setImage
import com.xwray.groupie.databinding.BindableItem

class SubEventSpeakerItem(
    private val context : Context,
    private val id: Int?,
    private val name: String?,
    private val orgPosition: String?,
    private val description: String?,
    private val avatar: String?,
    private val status: String?,
    private val onSpeakerClick: (id: Int) -> Unit
) : BindableItem<ItemSubEventSpeakerBinding>(id?.toLong() ?: 0) {

    private val speakerPosition = orgPosition?.replace("\n", " ")
    private val speakerDescription =
        if (description.isNullOrEmpty()) null
        else markWon(context).toMarkdown(description.replace("\n", " "))


    override fun bind(viewBinding: ItemSubEventSpeakerBinding, position: Int) {
        viewBinding.apply {
            ivSpeakerImage.apply {
                clipToOutline = true
                setImage(avatar, error = R.drawable.empty_speaker_avatar)
            }

            tvSpeakersName.text = name
            tvSpeakersPosition.apply {
                isVisible = !speakerPosition.isNullOrEmpty()
                text = speakerPosition
            }
            tvSpeakersDescription.apply {
                isVisible = !speakerDescription.isNullOrEmpty()
                text = speakerDescription
            }
            root.setOnClickListener {
                onSpeakerClick.invoke(id ?: 0)
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
            setOnClickListener { DefaultAlertDialog(context, null, mText) }
        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is SubEventSpeakerItem) return false
        if (name != other.name) return false
        if (orgPosition != other.orgPosition) return false
        if (description != other.description) return false
        if (avatar != other.avatar) return false
        if (status != other.status) return false
        return true
    }

    override fun getLayout(): Int = R.layout.item_sub_event_speaker
}