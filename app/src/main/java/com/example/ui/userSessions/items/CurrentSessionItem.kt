package com.example.ui.userSessions.items

import android.widget.ImageView
import com.example.R
import com.example.data.models.UserSessionModel
import com.example.databinding.ItemCurrentSessionBinding
import com.xwray.groupie.databinding.BindableItem
import setOnClickListener

class CurrentSessionItem(
    val session: UserSessionModel,
    val onKillSessions: () -> Unit,
    val onShowSession: (session : UserSessionModel) -> Unit
) : BindableItem<ItemCurrentSessionBinding>() {


    override fun bind(viewBinding: ItemCurrentSessionBinding, position: Int) {
        viewBinding.apply {
            tvDeviceName.text = session.deviceModel
            tvDeviceType.text = session.device
            tvLocation.text = session.location
            decorDeviceIcon(ivDeviceIcon, session.device)
            btnKillSessions.setOnClickListener {
                onKillSessions.invoke()
            }
            cardSession.setOnClickListener {
                onShowSession(session)
            }
        }
    }

    private fun decorDeviceIcon(icon: ImageView, device: String) {
        if (device.contains("android", true)) {
            icon.setImageResource(R.drawable.ic_android_device)
        } else if (device.contains("ios", true)) {
            icon.setImageResource(R.drawable.ic_apple_device)
        } else {
            icon.setImageResource(R.drawable.ic_desktop_device)
        }

    }

    override fun getLayout(): Int = R.layout.item_current_session
}