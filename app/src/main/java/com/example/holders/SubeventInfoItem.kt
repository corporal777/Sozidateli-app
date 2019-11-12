package com.example.holders

import android.text.util.Linkify
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.SubeventInfo
import com.example.extensions.defaultServerDateTimeFormatter
import com.example.extensions.formatToInterval
import com.example.extensions.substringToWholeWord
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_subevent_info.*
import maxLength
import me.saket.bettermovementmethod.BetterLinkMovementMethod

open class SubeventInfoItem(
        private val subevent: SubeventInfo
) : Item(subevent.id.toLong()) {

    private var ellipsizeDescription = true

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            tvTime.text = subevent.start.formatToInterval(subevent.finish, defaultServerDateTimeFormatter, true)
            tvTitle.text = subevent.title
            tvDescription.apply {
                text = subevent.description
                isVisible = !subevent.description.isNullOrEmpty()
            }

            val message = subevent.description ?: ""
            val ellipsizedMessage = if (ellipsizeDescription) message.substringToWholeWord(tvDescription.maxLength) else message

            tvDescription.apply {
                if (!ellipsizeDescription) maxLength = message.length
                isVisible = !subevent.description.isNullOrEmpty()
                text = ellipsizedMessage
                BetterLinkMovementMethod.linkify(Linkify.ALL, this)
            }

            btnReadMore.apply {
                isVisible = ellipsizedMessage != message
                setOnClickListener {
                    ellipsizeDescription = false
                    isVisible = false
                    tvDescription.maxLength = message.length
                    tvDescription.text = message
                }
            }

            tvLocation.apply {
                val locations = subevent.auditoriums.joinToString("\n") { it.name }
                text = locations
                isVisible = locations.isNotEmpty()
            }
        }
    }

    override fun getLayout() = R.layout.item_subevent_info
}