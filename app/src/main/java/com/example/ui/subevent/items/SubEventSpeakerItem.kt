package com.example.ui.subevent.items

import android.util.Log
import android.widget.ImageView
import androidx.core.view.isVisible
import com.example.R
import com.example.databinding.ItemSubEventSpeakerBinding
import com.example.ui.views.dialogs_new.MessageDialogWithBrownButton
import com.example.util.markWon
import com.example.util.setImage
import com.xwray.groupie.databinding.BindableItem

class SubEventSpeakerItem(
    private val name: String,
    private val orgPosition: String,
    private val description: String,
    private val avatar: String?,
    val status: String,
    val isRegistered: Boolean,
    private val onSpeakerClick: () -> Unit
) : BindableItem<ItemSubEventSpeakerBinding>() {

    private var speakerName = name
    private var speakerPosition = StringBuilder(orgPosition.replace("\n", " ")).toString()
    private var speakerDescription = StringBuilder(description.replace("\n", " ")).toString()

//    private var speakerDescription =
//        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."

    override fun bind(viewBinding: ItemSubEventSpeakerBinding, position: Int) {
        viewBinding.apply {

            ivSpeakerImage.setImage(avatar, error = R.drawable.empty_speaker_avatar)
            tvSpeakersName.text = speakerName
            if (speakerPosition.isNullOrEmpty()) {
                tvSpeakersPosition.isVisible = false
            } else {
                tvSpeakersPosition.isVisible = true
                tvSpeakersPosition.text = speakerPosition
            }

            if (!speakerName.isNullOrEmpty()){
                var linesCount = 10
                tvSpeakersName.setOnLayoutListener { n ->
                    if (n.lineCount > 0) {
                        linesCount -= n.lineCount
                        tvSpeakersDescription.apply {
                            maxLines = linesCount
                            markWon(context).setMarkdown(this, speakerDescription)
                            //text = speakerDescription
                        }
                    }
                }
            }

            root.setOnClickListener {
                onSpeakerClick.invoke()
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
        imageView.apply {
            isVisible = visibility
            setOnClickListener {
                MessageDialogWithBrownButton(context, mText)
            }
        }

    }

    override fun getLayout(): Int = R.layout.item_sub_event_speaker
}