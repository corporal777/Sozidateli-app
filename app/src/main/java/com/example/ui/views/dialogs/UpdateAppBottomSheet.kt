package com.example.ui.views.dialogs

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import androidx.core.view.isVisible
import com.example.R
import com.example.databinding.BottomSheetUpdateAppBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class UpdateAppBottomSheet(
    val context: Context,
    val isRequired: Boolean,
) {

    private val mDialog =
        if (isRequired) BottomSheetDialog(context, R.style.UpdateAppWithoutDimDialogTheme)
        else BottomSheetDialog(context)

    private val mBinding = BottomSheetUpdateAppBinding.inflate(LayoutInflater.from(context))
    private var onActionClick: () -> Unit = {}
    private var onDismissClick: () -> Unit = {}

    init {
        mDialog.apply {
            setContentView(mBinding.root)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            setCancelable(!isRequired)
        }

        mBinding.apply {
            clTop.isVisible = !isRequired
            tvTitle.text =
                if (isRequired) "Обновите приложение Созидатели, так как текущая версия устарела и больше не поддерживается."
                else "Доступна новая версия приложения. \n" + "Нажмите ниже, чтобы обновить."

            btnClose.setOnClickListener {
                if (!isRequired) mDialog.dismiss()
            }
            btnUpdate.apply {
                setOnClickListener {
                    openPlayMarket()
                    if (!isRequired) mDialog.dismiss()
                }
            }
        }
        if (!isRequired) {
            mDialog.setOnDismissListener { onDismissClick.invoke() }
        }
    }

    private fun openPlayMarket() {
        val appPackageName = context.packageName
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$appPackageName")
                )
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


    fun setUpdateClickCallback(block: () -> Unit): UpdateAppBottomSheet {
        onActionClick = block
        return this
    }

    fun setDismissCallback(block: () -> Unit): UpdateAppBottomSheet {
        onDismissClick = block
        return this
    }
}