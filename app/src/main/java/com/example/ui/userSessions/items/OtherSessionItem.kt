package com.example.ui.userSessions.items

import android.widget.ImageView
import com.example.R
import com.example.data.models.UserSessionModel
import com.example.databinding.ItemOtherSessionBinding
import com.example.ui.event.about.redesign.items.EventDetailImageItem
import com.xwray.groupie.databinding.BindableItem

class OtherSessionItem(
    val session: UserSessionModel?,
    val onSessionClick: (session: UserSessionModel) -> Unit
) : BindableItem<ItemOtherSessionBinding>() {


    private var deviceName = ""
    private var deviceType = ""
    private var deviceLocation = ""

    init {
        if (session != null) {
            deviceName =
                if (session.deviceModel.isNullOrEmpty() || session.deviceModel == "Device not defined") {
                    "Устройство не определено"
                } else {
                    session.deviceModel
                }
            deviceType = if (session.device.isNullOrEmpty()) {
                if (session.deviceModel.contains("iphone", true)) {
                    "Созидатели iOS, IP " + session.ipAddress
                } else {
                    "Устройство не определено"
                }
            } else {
                session.device + ", IP " + session.ipAddress
            }

            deviceLocation =
                if (session.location.isNullOrEmpty() || session.location.contains("Location not defined")) {
                    "Местоположение не определено"
                } else {
                    session.location
                }
        }
    }

    override fun bind(viewBinding: ItemOtherSessionBinding, position: Int) {
        viewBinding.apply {
            tvDeviceName.text = deviceName
            tvDeviceType.text = deviceType
            tvLocation.text = deviceLocation

            if (session != null)
                decorDeviceIcon(ivDeviceIcon, session)

            cardSession.setOnClickListener {
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