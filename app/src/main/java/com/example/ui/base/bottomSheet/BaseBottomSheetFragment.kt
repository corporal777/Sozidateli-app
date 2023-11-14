package com.example.ui.base.bottomSheet

import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.example.R
import com.example.data.models.UserDetail
import com.example.ui.base.BaseActivity
import com.example.ui.views.StateType
import com.example.ui.views.WarningDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import dagger.android.support.AndroidSupportInjection

abstract class BaseBottomSheetFragment<binding : ViewDataBinding>(
    val type: Int = 0,
    val lightDim: Boolean = false,
    val isTransparent : Boolean = false
) : MvpAppCompatBottomSheetFragment(), BaseBottomSheetContract.View {

    lateinit var mBinding: binding

    protected var mActivity: BaseActivity? = null
        private set

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity) this.mActivity = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        if (dialog is BottomSheetDialog) {
            dialog.behavior.skipCollapsed = true
            dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

            if (type == 1) { dialog.setOnShowListener { setupFullHeight(dialog) } }
            if (lightDim) dialog.window?.setDimAmount(0.3f)
        }
        return dialog
    }

    override fun getTheme(): Int {
        if (isTransparent) return R.style.TransparentBottomSheetDialogTheme
        else return super.getTheme()
    }

    private fun setupFullHeight(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet: FrameLayout =
            dialog!!.findViewById(com.google.android.material.R.id.design_bottom_sheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        showFullScreenBottomSheet(bottomSheet)
    }

    private fun showFullScreenBottomSheet(bottomSheet: FrameLayout) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = Resources.getSystem().displayMetrics.heightPixels - 50
        bottomSheet.layoutParams = layoutParams
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


    override fun showKeyboard(v: View?) {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun hideKeyboard(v: View?) {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        v?.let {
            imm.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }


    override fun hideBottomSheetDialog() = dismiss()

    fun focusOnInput(view: TextInputEditText, canShow: Boolean) {
        view.apply {
            post {
                showSoftInputOnFocus = canShow
                requestFocus()
                if (canShow) showKeyboard(this)
            }
        }
    }

    protected fun <T> showEditWarning(
        isBase: Boolean,
        isMax: Boolean,
        isEmptyBaseFields: Boolean,
        isEmptyMaxFields: Boolean,
        data: () -> T?
    ) {
        if ((isBase && isEmptyBaseFields) || (isMax && isEmptyMaxFields)) {
            WarningDialog(requireActivity(), resources.getString(R.string.warning_dialog_text))
                .setSelectCallback {
                    if (it) data.invoke()
                }
        } else data.invoke()
    }

    override fun showLoadingDialog() {
        mActivity?.showLoadingDialog()
    }

    override fun hideLoadingDialog() {
        mActivity?.hideLoadingDialog()
    }

    override fun showProgressBarLoadingDialog() {
        mActivity?.showProgressBarLoadingDialog()
    }

    override fun hideProgressBarLoadingDialog() {
        mActivity?.hideProgressBarLoadingDialog()
    }

    override fun hideAllLoadingDialogs() {
        mActivity?.hideAllLoadingDialogs()
    }

    override fun showCustomProgressDialog() {
        mActivity?.showCustomProgressDialog()
    }

    override fun hideCustomProgressDialog() {
        mActivity?.hideCustomProgressDialog()
    }

    override fun showCustomLoading() {
    }

    override fun hideCustomLoading() {
    }

    override fun showToast(message: String) {
        mActivity?.showToast(message)
    }

    override fun navigateUp() {
        mActivity?.navigateUp()
    }

    override fun showRequestErrorMessage() {
        mActivity?.showRequestErrorMessage()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding.unbind()
    }

    override fun onDetach() {
        mActivity = null
        super.onDetach()
    }

    @LayoutRes
    abstract fun layout(): Int

    companion object {
        const val FULLSCREEN = 1
    }
}