package com.example.ui.base

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import com.example.ui.views.toolbar.ToolbarContentActionBar
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

    override fun showDialog(message: String?) {
        mActivity?.showDialog(message)
    }

    override fun showDialog(message: String?, onOkClickListener: DialogInterface.OnClickListener?) {
        mActivity?.showDialog(message, onOkClickListener)
    }

    override fun showDialog(title: String?, message: String?, onOkClickListener: DialogInterface.OnClickListener?) {
        mActivity?.showDialog(title, message, onOkClickListener)
    }

    override fun showDialog(title: String?, message: String?) {
        mActivity?.showDialog(title, message)
    }

    override fun showToast(messagesIds: List<Int>) {
        mActivity?.showToast(messagesIds)
    }

    override fun showErrorDialog(messageIds: List<Int>, onDismissListener: DialogInterface.OnDismissListener?) {
        mActivity?.showErrorDialog(messageIds, onDismissListener)
    }

    override fun showNoInternetDialog() {
        mActivity?.showNoInternetDialog()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideKeyboard()
        hideAllLoadingDialogs()
    }
}