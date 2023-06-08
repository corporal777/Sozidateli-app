package com.example.ui.subevent.items

import android.util.Log
import android.widget.ImageView
import androidx.core.view.isVisible
import com.example.R
import com.example.databinding.ItemSubEventSpeakerBinding
import com.example.holders.redesign.EventActivityItem
import com.example.ui.views.dialogs_new.MessageDialogWithBrownButton
import com.example.util.markWon
import com.example.util.setImage
import com.xwray.groupie.databinding.BindableItem

class SubEventSpeakerItem(
    private val id: Int?,
    private val name: String?,
    private val orgPosition: String?,
    private val description: String?,
    private val avatar: String?,
    val status: String?,
    val isRegistered: Boolean,
    private val onSpeakerClick: (id: Int) -> Unit
) : BindableItem<ItemSubEventSpeakerBinding>(id?.toLong() ?: 0) {

    private val speakerName = name

    private val speakerPosition =
        if (orgPosition.isNullOrEmpty()) ""
        else StringBuilder(orgPosition.replace("\n", " ")).toString()

    private val speakerDescription =
        if (description.isNullOrEmpty()) ""
        else StringBuilder(description.replace("\n", " ")).toString()


    override fun bind(viewBinding: ItemSubEventSpeakerBinding, position: Int) {
        viewBinding.apply {
            ivSpeakerImage.setImage(avatar, error = R.drawable.empty_speaker_avatar)

            tvSpeakersName.text = speakerName
            tvSpeakersPosition.apply {
                isVisible = !speakerPosition.isNullOrEmpty()
                text = speakerPosition
            }
            tvSpeakersDescription.apply {
                markWon(context).setMarkdown(this, speakerDescription)
            }

//            if (!speakerName.isNullOrEmpty()){
//                var linesCount = 10
//                tvSpeakersName.setOnLayoutListener { n ->
//                    if (n.lineCount > 0) {
//                        linesCount -= n.lineCount
//                        tvSpeakersDescription.apply {
//                            maxLines = linesCount
//                            markWon(context).setMarkdown(this, speakerDescription)
//                            //text = speakerDescription
//                        }
//                    }
//                }
//            }

            root.setOnClickListener {
                onSpeakerClick.invoke(id ?: 0)
            }

            decorSpeakerStatus(status, ivSpeakerStatus)
        }
    }

    private fun decorSpeakerStatus(status: String?, imageView: ImageView) {
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
        if (other !is SubEventSpeakerItem) return false
        if (name != other.name) return false
        if (orgPosition != other.orgPosition) return false
        if (description != other.description) return false
        if (avatar != other.avatar) return false
        if (status != other.status) return false
        if (isRegistered != other.isRegistered) return false
        return true
    }

    override fun getLayout(): Int = R.layout.item_sub_event_speaker
}