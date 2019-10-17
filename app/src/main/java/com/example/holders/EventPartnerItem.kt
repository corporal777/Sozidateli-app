package com.example.holders

import androidx.core.view.isVisible
import com.example.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_event_partner.*

class EventPartnerItem(
        private val id: Int,
        private val logo: String?,
        private val name: String?
) : Item(id.toLong()) {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            tvName.text = name
            if (logo.isNullOrEmpty()) {
                ivLogo.isVisible = false
            } else {
                ivLogo.isVisible = true
                Picasso.get().load(logo).into(ivLogo, object : Callback {
                    override fun onSuccess() {
                        tvName.isVisible = false
                    }

                    override fun onError(e: Exception?) {
                        ivLogo.isVisible = false
                    }
                })
            }
            itemView.clipToOutline = true
        }
    }

    override fun getLayout() = R.layout.item_event_partner

    override fun getSpanSize(spanCount: Int, position: Int): Int {
        return spanCount / 2
    }
}