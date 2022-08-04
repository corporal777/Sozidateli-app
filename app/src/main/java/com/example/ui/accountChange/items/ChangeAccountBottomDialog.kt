package com.example.ui.accountChange.items

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.UserDetail
import com.example.data.models.UserSessionModel
import com.example.databinding.DialogChangeAccountBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ChangeAccountBottomDialog(
    val session: UserSessionModel,
    val user : UserDetail
) : BottomSheetDialogFragment() {

    private var _binding: DialogChangeAccountBinding? = null
    private val mBinding get() = _binding!!


    private var onLogoutClick: (pair : Pair<UserSessionModel, UserDetail>) -> Unit = {}
    private var onLogoutAndKillClick: (session: UserSessionModel) -> Unit = {}

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

        mBinding.tvKill.isVisible = false
        mBinding.tvLogoutAndKill.apply {
            setOnClickListener {
                onLogoutAndKillClick.invoke(session)
                dismiss()
            }
        }
        mBinding.tvLogout.setOnClickListener {
            onLogoutClick.invoke(Pair(session, user))
            dismiss()
        }
        mBinding.cardAction.setOnClickListener {
            dismiss()
        }
    }

    fun setLogoutCallback(block: (pair : Pair<UserSessionModel, UserDetail>) -> Unit): ChangeAccountBottomDialog {
        onLogoutClick = block
        return this
    }

    fun setLogoutAndKillCallback(block: (session: UserSessionModel) -> Unit): ChangeAccountBottomDialog {
        onLogoutAndKillClick = block
        return this
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}