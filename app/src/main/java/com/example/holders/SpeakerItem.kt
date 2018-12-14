package com.example.holders

import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.text.InputType
import com.example.R
import com.example.data.models.User
import com.example.ui.mySchedule.subevent.SubeventPresenter
import com.example.util.CropCircleTransformation
import com.squareup.picasso.Picasso
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_speaker.*
import kotlinx.android.synthetic.main.item_speaker.view.*

open class SpeakerItem(private val user: User,private val presenter:SubeventPresenter) : Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.apply {
            if(!user.image.isNullOrEmpty()) Picasso.get().load(user.image).transform(CropCircleTransformation()).into(ivSpeakerAvatar)
            tvSpeakerName.text = user.name
            tvSpeakerInfo.text = user.info
            val btnFavoriteBackground: Int
            val btnFavoriteTextColor: Int
            val btnFavoriteText: String
            if (user.subscribed) {
                btnFavoriteBackground = R.drawable.background_corners_border
                btnFavoriteTextColor = ContextCompat.getColor(context!!, R.color.colorAccent)
                btnFavoriteText = context.getString(R.string.remove_from_favorites)
            } else {
                btnFavoriteBackground = R.drawable.background_corners
                btnFavoriteTextColor = Color.WHITE
                btnFavoriteText =context.getString(R.string.add_to_favorites)
            }

            btnSubscribe.apply {
                setBackgroundResource(btnFavoriteBackground)
                setTextColor(btnFavoriteTextColor)
                text = btnFavoriteText
            }

            setOnClickListener {
                presenter.onSpeakerClick(user)
            }
        }
    }

    override fun getLayout() = R.layout.item_speaker
}