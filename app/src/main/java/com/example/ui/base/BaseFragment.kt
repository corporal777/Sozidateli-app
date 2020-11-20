package com.example.ui.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.R
import com.example.ui.views.ApiErrorDialog
import com.example.ui.views.FillProfileDialog
import dagger.android.support.AndroidSupportInjection

abstract class BaseFragment : MvpAppCompatFragment(), BaseContract.View {

    protected var mActivity: BaseActivity? = null
        private set

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity) {
            this.mActivity = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layout(), container, false)
    }

    override fun onDetach() {
        mActivity = null
        super.onDetach()
    }

    @LayoutRes
    abstract fun layout(): Int

    override fun showToast(@StringRes message: Int) = showToast(getString(message))

    override fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun showLoadingDialog() {
        mActivity?.showLoadingDialog()
    }

    override fun hideLoadingDialog() {
        mActivity?.hideLoadingDialog()
    }

    override fun hideAllLoadingDialogs() {
        mActivity?.hideAllLoadingDialogs()
    }

    override fun hideKeyboard() {
        mActivity?.hideKeyboard()
    }

    override fun hideKeyboard(v: View?) {
        mActivity?.hideKeyboard(v)
    }

    override fun showKeyboard() {
        mActivity?.showKeyboard()
    }

    fun showKeyboard(view: View) {
        mActivity?.showKeyboard(view)
    }

    override fun navigateUp() {
        mActivity?.navigateUp()
    }

    override fun showNoConnectionMessage(show: Boolean) {
        mActivity?.showNoConnectionMessage(show)
    }

    override fun showRequestErrorMessage() {
        mActivity?.showRequestErrorMessage()
    }

    override fun showEmailErrorMessage() {
        context?.let {
            ApiErrorDialog(it, getString(R.string.email_exist_error_title),
                    getString(R.string.email_exist_error_text))
                .setSelectCallback {  }
        }
    }

    override fun showNotificationErrorMessage() {
        context?.let { FillProfileDialog(it).setSelectCallback { findNavController().navigate(R.id.user_profile_fragment) } }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideKeyboard()
        hideAllLoadingDialogs()
    }
}