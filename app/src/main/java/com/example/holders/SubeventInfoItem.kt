package com.example.holders

import android.text.util.Linkify
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.SubeventInfo
import com.example.extensions.defaultServerDateTimeFormatter
import com.example.extensions.formatToInterval
import com.example.ui.views.UserSubscribeButton
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_subevent_info.*
import maxLength
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import setOnClickListener

open class SubeventInfoItem(
        private val subevent: SubeventInfo,
        private val onFavoriteClickListener: () -> Unit
) : Item(subevent.id.toLong()) {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            tvTime.text = subevent.start.formatToInterval(subevent.finish, defaultServerDateTimeFormatter, true)
            tvTitle.text = subevent.title
            tvDescription.apply {
                text = subevent.description
                isVisible = !subevent.description.isNullOrEmpty()
            }

            val message = subevent.description

            tvDescription.apply {
                isVisible = !message.isNullOrEmpty()
                text = message
                BetterLinkMovementMethod.linkify(Linkify.ALL, this)
            }

            tvLocation.apply {
                val locations = subevent.auditoriums.joinToString("\n") { it.name }
                text = locations
                isVisible = locations.isNotEmpty()
            }

            btnSubscribe.apply {
                setAction(this, subevent.isInFavorites)
                setOnClickListener(onFavoriteClickListener)
            }
        }
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int, payloads: MutableList<Any>) {
        val payload = payloads.firstOrNull()
        if (payload == null) super.bind(viewHolder, position, payloads)
        else {
            if (payload is Boolean) setAction(viewHolder.btnSubscribe, payload)
        }
    }

    private fun setAction(button: UserSubscribeButton, isInFavorites: Boolean) {
        button.setAction(if (isInFavorites) UserSubscribeButton.Action.UNFAVORITE else UserSubscribeButton.Action.FAVORITE)
    }

    override fun getLayout() = R.layout.item_subevent_info
}