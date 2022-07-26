package com.example.ui.views.dialogs_new

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.UserDetail
import com.example.data.models.UserSessionModel
import com.example.databinding.BottomSheetCalendarBinding
import com.example.databinding.BottomSheetSessionBinding
import com.example.databinding.DialogChangeAccountBinding
import com.example.util.getDeviceId
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import setOnClickListener
import java.util.*

class ChangeAccountBottomDialog(
    val isCurrentUser: Boolean,
    val session: UserSessionModel
) : BottomSheetDialogFragment() {

    private var _binding: DialogChangeAccountBinding? = null
    private val mBinding get() = _binding!!
    private var onActionClick: (session: UserSessionModel, type : LogoutType) -> Unit = {
            _: UserSessionModel, _: LogoutType ->

    }

    private var onLogoutClick: (session: UserSessionModel) -> Unit = {}
    private var onLogoutAndKillClick: (session: UserSessionModel) -> Unit = {}
    private var onKillClick: (session: UserSessionModel) -> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.TransparentBottomSheetDialogTheme);
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogChangeAccountBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isCurrentUser) {
            mBinding.tvLogoutAndKill.apply {
                isVisible = true
                setOnClickListener {
                    onLogoutAndKillClick.invoke(session)
                    dismiss()
                }
            }
        }
        if (!session.isLogged){
            mBinding.apply {
                tvLogoutAndKill.isVisible = false
                tvLogout.isVisible = false
                tvKill.apply {
                    isVisible = true
                    setOnClickListener {
                        onKillClick.invoke(session)
                        dismiss()
                    }
                }
            }
        }
        mBinding.tvLogout.setOnClickListener {
            onLogoutClick.invoke(session)
            dismiss()
        }
        mBinding.cardAction.setOnClickListener {
            dismiss()
        }
    }

    fun setLogoutCallback(block : (session: UserSessionModel) -> Unit): ChangeAccountBottomDialog{
        onLogoutClick = block
        return this
    }

    fun setLogoutAndKillCallback(block : (session: UserSessionModel) -> Unit): ChangeAccountBottomDialog{
        onLogoutAndKillClick = block
        return this
    }

    fun setKillCallback(block : (session: UserSessionModel) -> Unit): ChangeAccountBottomDialog{
        onKillClick = block
        return this
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    enum class LogoutType{
        LOGOUT, LOGOUT_AND_KILL, KILL
    }

}