package com.example.holders

import android.graphics.Color
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.models.Speaker
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_speaker.view.*
import setCircleImage

open class SpeakerItem(
        private var speaker: Speaker,
        private val onSpeakerClick: (Speaker) -> Unit,
        private val onFavoriteChangeClick: (Speaker) -> Unit
) : Item(speaker.id.toLong()) {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.apply {
            ivSpeakerAvatar.setCircleImage(speaker.photo, R.drawable.avatar_placeholder)

            tvSpeakerName.text = speaker.name
            tvSpeakerInfo.text = speaker.position

            btnSubscribe.apply {
                text = if (speaker.isInFavorite) {
                    setBackgroundResource(R.drawable.background_corners_border)
                    setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
                    context.getString(R.string.remove_from_favorites)
                } else {
                    setBackgroundResource(R.drawable.background_corners)
                    setTextColor(Color.WHITE)
                    context.getString(R.string.add_to_favorites)
                }

                setOnClickListener { onFavoriteChangeClick(speaker) }
            }

            setOnClickListener { onSpeakerClick(speaker) }
        }
    }

    fun updateSpeaker(speaker: Speaker) {
        this.speaker = speaker
        notifyChanged()
    }

    override fun getLayout() = R.layout.item_speaker
}