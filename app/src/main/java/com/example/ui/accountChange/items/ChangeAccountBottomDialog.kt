package com.example.ui.accountChange.items

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.UserDetail
import com.example.data.models.UserSessionModel
import com.example.databinding.BottomSheetUpdateAppBinding
import com.example.databinding.DialogChangeAccountBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.type.Color
import kotlinx.android.synthetic.main.popup_menu.*

class ChangeAccountBottomDialog(
    context: Context,
    val session: UserSessionModel
) : BottomSheetDialog(context, R.style.TransparentBottomSheetDialogTheme) {

    private val mBinding = DialogChangeAccountBinding.inflate(LayoutInflater.from(context))

    private var onLogoutClick: (session : UserSessionModel) -> Unit = {}
    private var onLogoutAndKillClick: (session: UserSessionModel) -> Unit = {}
    private var onKillClick: (session: UserSessionModel) -> Unit = {}

    init {
        setContentView(mBinding.root)
        if (session.isLogged){
            mBinding.tvKill.isVisible = false
            mBinding.tvLogoutAndKill.isVisible = true
            mBinding.tvLogout.isVisible = true
        }else {
            mBinding.tvKill.isVisible = true
            mBinding.tvLogoutAndKill.isVisible = false
            mBinding.tvLogout.isVisible = false
        }
        mBinding.tvLogoutAndKill.setOnClickListener {
            onLogoutAndKillClick.invoke(session)
            dismiss()
        }
        mBinding.tvLogout.setOnClickListener {
            onLogoutClick.invoke(session)
            dismiss()
        }
        mBinding.tvKill.setOnClickListener {
            onKillClick.invoke(session)
            dismiss()
        }
        mBinding.cardAction.setOnClickListener {
            dismiss()
        }
    }


    fun setLogoutCallback(block: (session : UserSessionModel) -> Unit): ChangeAccountBottomDialog {
        onLogoutClick = block
        return this
    }

    fun setLogoutAndKillCallback(block: (session: UserSessionModel) -> Unit): ChangeAccountBottomDialog {
        onLogoutAndKillClick = block
        return this
    }

    fun setKillCallback(block: (session: UserSessionModel) -> Unit): ChangeAccountBottomDialog {
        onKillClick = block
        return this
    }
}