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

class KillAccountBottomDialog(
    val session: UserSessionModel,
    val user : UserDetail
) : BottomSheetDialogFragment() {

    private var _binding: DialogChangeAccountBinding? = null
    private val mBinding get() = _binding!!

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


        mBinding.tvLogoutAndKill.isVisible = false
        mBinding.tvLogout.isVisible = false
        mBinding.tvKill.apply {
            isVisible = true
            setOnClickListener {
                onKillClick.invoke(session)
                dismiss()
            }
        }
        mBinding.cardAction.setOnClickListener {
            dismiss()
        }
    }

    fun setKillCallback(block: (session: UserSessionModel) -> Unit): KillAccountBottomDialog {
        onKillClick = block
        return this
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}