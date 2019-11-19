package com.example.ui.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.Notification
import com.example.ui.notification.NotificationFragment
import com.example.ui.notification.NotificationFragmentArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.layout_inapp.*


class InappDialog private constructor() : BottomSheetDialogFragment() {

    private lateinit var inapp: Notification
    private lateinit var notificationFragment: NotificationFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inapp = arguments!!.getParcelable(ARG_INAPP)!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_inapp, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager.beginTransaction()
                .replace(flNotificationContainer.id, createNotificationFragment())
                .commitNowAllowingStateLoss()

        when (inapp.type) {
            Notification.Type.SIMPLE -> {
                btnPositive.apply {
                    isVisible = true
                    text = getString(R.string.ok)
                }
            }
            Notification.Type.ACCEPTABLE -> {
                btnPositive.apply {
                    isVisible = true
                    text = getString(R.string.notifications_accept)
                    setOnClickListener { notificationFragment.presenter.onNotificationAcceptClick() }
                }
                btnNegative.apply {
                    isVisible = true
                    text = getString(R.string.notifications_cancel)
                    setOnClickListener { notificationFragment.presenter.onNotificationCancelClick() }
                }
            }
            Notification.Type.RATE -> {

            }
        }

        requireBottomSheetDialog().apply {
            behavior.apply {
                state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    private fun requireBottomSheetDialog(): BottomSheetDialog {
        return requireDialog() as? BottomSheetDialog
                ?: throw IllegalStateException("Dialog is not BottomSheetDialog")
    }

    private fun createNotificationFragment(): NotificationFragment {
        return NotificationFragment().apply {
            arguments = NotificationFragmentArgs.Builder(inapp).apply { showButtons = false }.build().toBundle()
            notificationFragment = this
        }
    }

    companion object {
        private const val ARG_INAPP = "inapp"

        fun newInstance(inapp: Notification): InappDialog {
            return InappDialog().apply {
                arguments = bundleOf(ARG_INAPP to inapp)
            }
        }
    }
}