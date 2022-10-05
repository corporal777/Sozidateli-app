package com.example.ui.base

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.example.data.models.UserDetail
import com.example.ui.views.StateType
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import dagger.android.support.AndroidSupportInjection

abstract class BaseBottomSheetFragment<binding : ViewDataBinding> : BottomSheetDialogFragment() {

    lateinit var mBinding: binding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        if (dialog is BottomSheetDialog) {
            dialog.behavior.skipCollapsed = true
            dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (::mBinding.isInitialized.not()) {
            mBinding = DataBindingUtil.inflate(layoutInflater, layout(), container, false)
            mBinding.lifecycleOwner = this
        }
        return mBinding.root
    }

    fun showKeyboard(view: View) {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }



    fun hideKeyboard(v: View?) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        v?.let {
            imm.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    fun focusOnInput(view : TextInputEditText, canShow: Boolean) {
        view.apply {
            post {
                showSoftInputOnFocus = canShow
                requestFocus()
                if (canShow) showKeyboard(this)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding.unbind()
    }

    @LayoutRes
    abstract fun layout(): Int
}