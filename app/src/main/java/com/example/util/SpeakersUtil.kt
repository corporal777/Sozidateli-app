package com.example.util

import android.graphics.Color
import androidx.core.content.ContextCompat
import com.example.R
import com.example.adapters.SimplePagingRecyclerViewAdapter
import com.example.adapters.ViewHolder
import com.example.data.models.Speaker
import kotlinx.android.synthetic.main.item_speaker.*
import setCircleImageWithPlaceholder

class SpeakersUtil{
    companion object {
        fun getSpeakersAdapter(btnSubscribeClick:(item:Speaker)->Unit,itemClick:(item:Speaker)->Unit): SimplePagingRecyclerViewAdapter<Speaker> {
            return object : SimplePagingRecyclerViewAdapter<Speaker>(
                    { oldItem, newItem -> oldItem.id == newItem.id },
                    { oldItem, newItem -> oldItem == newItem }
            ) {
                override fun getItemLayout(itemView: Int) = R.layout.item_speaker

                override fun onBindItem(viewHolder: ViewHolder, item: Speaker?, position: Int) {
                    item!!
                    viewHolder.apply {
                        ivSpeakerAvatar.setCircleImageWithPlaceholder(item.photo, R.drawable.avatar_placeholder)

                        tvSpeakerName.text = item.name
                        tvSpeakerInfo.text = item.description

                        val btnFavoriteBackground: Int
                        val btnFavoriteTextColor: Int
                        val btnFavoriteText: String

                        if (item.isInFavorite) {
                            btnFavoriteBackground = R.drawable.background_corners_border
                            btnFavoriteTextColor = ContextCompat.getColor(viewHolder.itemView.context, R.color.colorAccent)
                            btnFavoriteText = viewHolder.itemView.context.getString(R.string.remove_from_favorites)
                        } else {
                            btnFavoriteBackground = R.drawable.background_corners
                            btnFavoriteTextColor = Color.WHITE
                            btnFavoriteText = viewHolder.itemView.context.getString(R.string.add_to_favorites)
                        }

                        btnSubscribe.apply {
                            setBackgroundResource(btnFavoriteBackground)
                            setTextColor(btnFavoriteTextColor)
                            text = btnFavoriteText
                            setOnClickListener { btnSubscribeClick(item) }
                        }

                        itemView.setOnClickListener { itemClick(item) }
                    }
                }
            }
        }
    }
}