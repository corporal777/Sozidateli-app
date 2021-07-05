package com.example.holders

import android.content.res.ColorStateList
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.Event
import com.example.data.models.EventNew
import com.example.ui.views.UserSubscribeButton
import com.squareup.picasso.Picasso
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_event_favorite.*
import parseColor
import setOnClickListener

class EventFavoriteItem(
        val event: /*Event*/EventNew,
        private val onEventClick: () -> Unit,
        private val onEventActionClick: () -> Unit,
        private val onEventSubeventsClick: () -> Unit
) : Item(event.id?.toLong()?: 0) {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            tvEventName.text = event.name
            ivLogo.apply {
                clipToOutline = true
                Picasso.get().load(event.image?.uri.let { if (it.isNullOrBlank()) null else it }/*event.backgroundImage.let { if (it.isNullOrBlank()) null else it }*/)
                        .into(this)
            }

            tvImageName.apply {
                text = event.name
                clipToOutline = true
                ViewCompat.setBackgroundTintList(this, ColorStateList.valueOf(event.backgroundColor?.value.parseColor()
                        ?: ResourcesCompat.getColor(resources, R.color.colorAccent, null)))
            }

            btnSubevents.apply {
                isVisible = event.binds?.userFavoriteActivities != null
                //isVisible = event.activities?.any { it.isInFavorites } ?: false
                setOnClickListener(onEventSubeventsClick)
            }
            userSubscribeButton.apply {
                setAction(this, event.binds?.userFavorite != null)
                setOnClickListener(onEventActionClick)
            }

            itemView.setOnClickListener { onEventClick.invoke() }
        }
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int, payloads: MutableList<Any>) {
        val payload = payloads.firstOrNull()
        if (payload == null) super.bind(viewHolder, position, payloads)
        else {
            if (payload is Boolean) setAction(viewHolder.userSubscribeButton, payload)
        }
    }

    private fun setAction(button: UserSubscribeButton, isFavorite: Boolean) {
        button.setAction(if (isFavorite) UserSubscribeButton.Action.UNFAVORITE
        else UserSubscribeButton.Action.FAVORITE)
    }

    override fun getLayout() = R.layout.item_event_favorite
}