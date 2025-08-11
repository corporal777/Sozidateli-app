package com.example.ui.views.dialogs

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import androidx.core.view.isVisible
import com.example.app.R
import com.example.app.databinding.BottomSheetUpdateAppBinding
import com.example.extensions.getDrawable
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class UpdateAppBottomSheetDialog(val context: Context, val isRequired: Boolean) {

    private val mDialog =
        if (isRequired) BottomSheetDialog(context, R.style.UpdateAppWithoutDimDialogTheme)
        else BottomSheetDialog(context, R.style.TransparentBottomSheetDialogTheme)

    private val mBinding = BottomSheetUpdateAppBinding.inflate(LayoutInflater.from(context))
    private var onActionClick: () -> Unit = {}
    private var onDismissClick: () -> Unit = {}

    init {
        mDialog.apply {
            setContentView(mBinding.root)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
            behavior.isDraggable = !isRequired
            setCancelable(!isRequired)
        }

        mBinding.apply {
            clContent.apply {
                if (isRequired) setBackgroundColor(context.getColor(R.color.main_background))
                else background = getDrawable(R.drawable.background_bottom_sheet)
            }

            tvTitle.text =
                if (isRequired) context.getString(R.string.app_update_required_text)
                else context.getString(R.string.app_update_not_required_text)

            viewSize.isVisible = isRequired
            viewTop.isVisible = !isRequired
            btnClose.apply {
                isVisible = !isRequired
                setOnClickListener { mDialog.dismiss() }
            }
            btnUpdate.setOnClickListener {
                openPlayMarket()
                if (!isRequired) mDialog.dismiss()
            }
        }
        if (!isRequired) mDialog.setOnDismissListener { onDismissClick.invoke() }
    }

    private fun openPlayMarket() {
        val appPackageName = context.packageName
        try {
            context.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName"))
            )
        } catch (e: android.content.ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                )
            )
        }
    }

    fun show() = mDialog.show()


    fun setUpdateClickCallback(block: () -> Unit): UpdateAppBottomSheetDialog {
        onActionClick = block
        return this
    }

    fun setDismissCallback(block: () -> Unit): UpdateAppBottomSheetDialog {
        onDismissClick = block
        return this
    }
}