package com.example.ui.views.dialogs

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import com.example.R
import com.example.data.models.UserSessionModel
import com.example.databinding.BottomSheetSessionBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class SessionBottomSheet(
    context: Context,
    val isCurrentSession: Boolean,
    val session: UserSessionModel
) : BottomSheetDialog(context) {

    private val mBinding = BottomSheetSessionBinding.inflate(LayoutInflater.from(context))
    private var onActionClick: () -> Unit = {}

    private val deviceName = session.deviceNameWithVersion
    private val deviceIp = session.deviceIp
    private val deviceLocation = session.deviceLocation

    init {
        setContentView(mBinding.root)

        mBinding.apply {
            tvDeviceLabel.apply {
                text = if (deviceName.contains("iOS", true)) context.getString(R.string.app_label)
                else if (deviceName.contains("android", true)) context.getString(R.string.app_label)
                else context.getString(R.string.browser_label)
            }
            tvDevice.text = deviceName
            tvIpAddress.text = deviceIp
            tvLocation.text = deviceLocation

            decorDeviceIcon(ivDeviceIcon, session)

            btnAction.apply {
                text = if (isCurrentSession) context.getString(R.string.kill_all_other_sessions)
                else context.getString(R.string.kill_session_label)

                setOnClickListener {
                    onActionClick.invoke()
                    dismiss()
                }
            }
            btnClose.setOnClickListener {
                dismiss()
            }
        }
    }


    private fun decorDeviceIcon(icon: ImageView, session: UserSessionModel) {
        if (session.device.isNullOrEmpty()) {
            if (session.deviceModel.contains("iphone", true)) {
                icon.setImageResource(R.drawable.ic_apple_device)
            } else icon.setImageResource(R.drawable.ic_desktop_device)
        } else {
            if (session.device.contains("android", true)) {
                icon.setImageResource(R.drawable.ic_android_device)
            } else if (session.device.contains("ios", true)) {
                icon.setImageResource(R.drawable.ic_apple_device)
            } else icon.setImageResource(R.drawable.ic_desktop_device)

        }
    }

    fun setOnKillSession(block: () -> Unit): SessionBottomSheet {
        onActionClick = block
        return this
    }
}