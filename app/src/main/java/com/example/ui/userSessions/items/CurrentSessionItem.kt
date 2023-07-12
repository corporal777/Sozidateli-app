package com.example.ui.userSessions.items

import android.widget.ImageView
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.EventActivityModel
import com.example.data.models.UserSessionModel
import com.example.databinding.ItemCurrentSessionBinding
import com.example.databinding.ItemLectureBinding
import com.xwray.groupie.databinding.BindableItem
import setOnClickListener

class CurrentSessionItem(
    val session: UserSessionModel,
    val isHas : Boolean,
    val onKillSessions: () -> Unit,
    val onShowSession: (session: UserSessionModel) -> Unit
) : BindableItem<ItemCurrentSessionBinding>(session.sessionId) {

    private var deviceName = ""
    private var deviceType = ""
    private var deviceLocation = ""

    private var isHasSession = isHas

    init {
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

    override fun bind(viewBinding: ItemCurrentSessionBinding, position: Int) {
        viewBinding.apply {
            tvDeviceName.text = deviceName
            tvDeviceType.text = deviceType
            tvLocation.text = "$deviceLocation • в сети"
            decorDeviceIcon(ivDeviceIcon, session)
            btnKillSessions.apply {
                isVisible = isHasSession
                setOnClickListener {
                    onKillSessions.invoke()
                }
            }
            itemContainer.setOnClickListener {
                onShowSession(session)
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


    override fun bind(viewBinding: ItemCurrentSessionBinding, position: Int, payloads: MutableList<Any>?) {
        val payload = payloads?.firstOrNull()
        if (payload == null) super.bind(viewBinding, position, payloads)
        else {
            if (payload is Boolean) {
                isHasSession = payload
                viewBinding.btnKillSessions.isVisible = isHasSession
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_current_session
}