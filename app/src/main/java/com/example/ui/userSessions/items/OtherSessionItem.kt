package com.example.ui.userSessions.items

import android.widget.ImageView
import com.example.app.R
import com.example.data.models.UserSessionModel
import com.example.app.databinding.ItemOtherSessionBinding
import com.xwray.groupie.databinding.BindableItem

class OtherSessionItem(
    val session: UserSessionModel?,
    val onSessionClick: (session: UserSessionModel) -> Unit
) : BindableItem<ItemOtherSessionBinding>(session?.sessionId ?: 0) {

    private val deviceName = session?.deviceName
    private val deviceType = session?.deviceType
    private val deviceLocation = session?.deviceLocation

    override fun bind(viewBinding: ItemOtherSessionBinding, position: Int) {
        viewBinding.apply {
            tvDeviceName.text = deviceName
            tvDeviceType.text = deviceType
            tvLocation.text = deviceLocation + " • " + session?.sessionDate

            if (session != null) decorDeviceIcon(ivDeviceIcon, session)

            itemContainer.setOnClickListener {
                onSessionClick(session!!)
            }
        }
    }


    private fun decorDeviceIcon(icon: ImageView, session: UserSessionModel) {
        if (session.device.isNullOrEmpty()) {
            if (session.deviceModel.contains("iphone", true)) {
                icon.setImageResource(R.drawable.ic_apple_device)
            } else {
                icon.setImageResource(R.drawable.ic_desktop_device)
            }
        } else {
            if (session.device.contains("android", true)) {
                icon.setImageResource(R.drawable.ic_android_device)
            } else if (session.device.contains("ios", true)) {
                icon.setImageResource(R.drawable.ic_apple_device)
            } else {
                icon.setImageResource(R.drawable.ic_desktop_device)
            }
        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is OtherSessionItem) return false
        if (session != other.session) return false
        return true
    }

    override fun getLayout(): Int = R.layout.item_other_session
}