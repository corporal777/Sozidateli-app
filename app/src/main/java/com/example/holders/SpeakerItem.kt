package com.example.holders

import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.Speaker
import com.example.ui.views.UserSubscribeButton
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_speaker.view.*
import setCircleImage

open class SpeakerItem(
        private var speaker: Speaker,
        private val onSpeakerClick: (Speaker) -> Unit,
        private val onFavoriteChangeClick: (Speaker) -> Unit
) : Item(speaker.id.toLong()) {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.apply {
            ivSpeakerAvatar.setCircleImage(speaker.user.user_avatar, R.drawable.avatar_placeholder)

            tvSpeakerName.text = speaker.user.fullName
            tvSpeakerInfo.apply {
                text = speaker.description
                isVisible = !speaker.description.isNullOrEmpty()
            }

            btnAction.apply {
                btnAction.setAction(if (speaker.user.is_in_favorite) UserSubscribeButton.Action.UNFAVORITE else UserSubscribeButton.Action.FAVORITE)
                setOnClickListener { onFavoriteChangeClick(speaker) }
            }

            setOnClickListener { onSpeakerClick(speaker) }
        }
    }

    fun updateSpeaker(speaker: Speaker) {
        this.speaker = speaker
        notifyChanged()
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (this === other) return true
        if (other !is SpeakerItem) return false

        if (speaker != other.speaker) return false

        return true
    }

    override fun getLayout() = R.layout.item_speaker
}