package com.example.holders

import android.content.res.ColorStateList
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.Event
import com.example.ui.views.UserSubscribeButton
import com.squareup.picasso.Picasso
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_event_favorite.*
import parseColor

class EventFavoriteItem(
        private val event: Event,
        private val onEventClick: () -> Unit
) : Item(event.id.toLong()) {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            tvEventName.text = event.name
            ivLogo.apply {
                clipToOutline = true
                Picasso.get().load(event.logo.let { if (it.isNullOrBlank()) null else it })
                        .into(this)
            }

            tvImageName.apply {
                text = event.name
                clipToOutline = true
                ViewCompat.setBackgroundTintList(this, ColorStateList.valueOf(event.backgroundColor.parseColor()
                        ?: ResourcesCompat.getColor(resources, R.color.colorAccent, null)))
            }

            btnSubevents.isVisible = event.activities?.any { it.isInFavorites } ?: false
            userSubscribeButton.setAction(if (event.isInFavorites) UserSubscribeButton.Action.UNFAVORITE
            else UserSubscribeButton.Action.FAVORITE)

            itemView.setOnClickListener { onEventClick.invoke() }
        }
    }

    override fun getLayout() = R.layout.item_event_favorite
}