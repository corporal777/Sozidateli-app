package com.example.holders

import com.example.R
import com.example.ui.views.UserSubscribeButton
import com.example.ui.views.UserSubscribeButton.Companion.ACTION_SUBSCRIBE
import com.example.ui.views.UserSubscribeButton.Companion.ACTION_UNBLOCK
import com.example.ui.views.UserSubscribeButton.Companion.ACTION_UNSUBSCRIBE
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_profile_data_user.*
import setCircleImage

class ProfileDataUserItem(
        id: Long,
        private val avatar: String?,
        private val name: String,
        private val uid: Int,
        private val subscribeAction: Int,
        private val actionClickListener: (Int) -> Unit,
        private val writeMessageClickListener: () -> Unit
) : Item(id) {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            ivAvatar.setCircleImage(avatar, R.drawable.avatar_placeholder)
            tvName.text = name
            tvId.apply { text = resources.getString(R.string.profile_uid, uid) }
            btnAction.apply {
                setAction(this, subscribeAction)
                setOnClickListener { actionClickListener(this.action) }
            }

            btnChat.setOnClickListener { writeMessageClickListener() }
        }
    }

    override fun bind(holder: ViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.isEmpty()) super.bind(holder, position, payloads)
        else holder.apply { (payloads[0] as? Int)?.let { setAction(btnAction, it) } }
    }

    private fun setAction(button: UserSubscribeButton, action: Int) {
        when (action) {
            ACTION_UNBLOCK -> button.setActionUnblock()
            ACTION_SUBSCRIBE -> button.setActionSubscribe()
            ACTION_UNSUBSCRIBE -> button.setActionUnsubscribe()
        }
    }

    override fun getLayout() = R.layout.item_profile_data_user
}