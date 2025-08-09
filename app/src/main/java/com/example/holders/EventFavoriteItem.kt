package com.example.holders

import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import com.example.app.R
import com.example.app.databinding.ItemEventFavoriteBinding
import com.example.data.models.EventNew
import com.example.common.parseColor
import com.example.ui.views.UserSubscribeButton
import com.squareup.picasso.Picasso
import com.xwray.groupie.viewbinding.BindableItem


class EventFavoriteItem(
        val event: EventNew,
        private val onEventClick: () -> Unit,
        private val onEventActionClick: () -> Unit,
        private val onEventSubeventsClick: () -> Unit
) : BindableItem<ItemEventFavoriteBinding>(event.id?.toLong()?: 0) {

    override fun bind(viewBinding: ItemEventFavoriteBinding, position: Int) {
        viewBinding.apply {
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

//            btnSubevents.apply {
//                isVisible = !event.binds?.userFavoriteActivities.isNullOrEmpty()
//                isVisible = event.activities?.any { it.isInFavorites } ?: false
//                setOnClickListener(onEventSubeventsClick)
//            }
            userSubscribeButton.apply {
                setAction(this, event.binds?.userFavorite != null)
                setOnClickListener(onEventActionClick)
            }

            root.setOnClickListener { onEventClick.invoke() }
        }
    }

    override fun bind(
        viewBinding: ItemEventFavoriteBinding,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val payload = payloads?.firstOrNull()
        if (payload == null) super.bind(viewBinding, position, payloads)
        else {
            if (payload is Boolean) setAction(viewBinding.userSubscribeButton, payload)
        }
    }

    private fun setAction(button: UserSubscribeButton, isFavorite: Boolean) {
        button.setAction(if (isFavorite) UserSubscribeButton.Action.UNFAVORITE
        else UserSubscribeButton.Action.FAVORITE)
    }

    override fun initializeViewBinding(view: View) = ItemEventFavoriteBinding.bind(view)
    override fun getLayout() = R.layout.item_event_favorite
}