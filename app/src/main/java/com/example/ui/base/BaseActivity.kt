package com.example.ui.base

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.example.R
import dagger.android.AndroidInjection
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

abstract class BaseActivity : MvpAppCompatActivity(), BaseContract.View {

    private var countVisibleLoading = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(layout())
        getLoadingView().setOnTouchListener { _, _ -> return@setOnTouchListener true }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }


    override fun showLoadingDialog() {
        if (!isFinishing) runOnUiThread {
            countVisibleLoading++
            getLoadingView().visibility = View.VISIBLE
        }
    }

    override fun hideLoadingDialog() {
        if (!isFinishing) runOnUiThread {
            countVisibleLoading--
            if (countVisibleLoading <= 0) {
                countVisibleLoading = 0
                getLoadingView().visibility = View.GONE
            }

        }
    }

    override fun hideAllLoadingDialogs() {
        if (!isFinishing) runOnUiThread {
            countVisibleLoading = 0
            getLoadingView().visibility = View.GONE
        }
    }

    override fun hideKeyboard() {
        hideKeyboard(currentFocus)
    }

    override fun hideKeyboard(v: View?) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        v?.let {
            imm.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    override fun showKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    fun showKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun showDialog(message: String?) {
        showDialog(null, message, null)
    }

    override fun showDialog(message: String?, onOkClickListener: DialogInterface.OnClickListener?) {
        showDialog(null, message, onOkClickListener)
    }

    override fun showDialog(title: String?, message: String?, onOkClickListener: DialogInterface.OnClickListener?) {
        AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.ok, onOkClickListener)
                .create()
                .show()
    }

    override fun showDialog(title: String?, message: String?) {
        showDialog(title, message, null)
    }

    override fun showToast(messagesIds: List<Int>) {
        val messages = messagesIds.map { getString(it) }
        showToast(messages.joinToString(separator = "\n"))
    }

    override fun showErrorDialog(messageIds: List<Int>, onDismissListener: DialogInterface.OnDismissListener?) {
        val messages = messageIds.map { getString(it) }

        AlertDialog.Builder(this)
                .setTitle(R.string.error_title)
                .setMessage(messages.joinToString(separator = "\n"))
                .setPositiveButton(R.string.ok, null)
                .setOnDismissListener(onDismissListener)
                .create()
                .show()
    }

    override fun showNoInternetDialog() {
        AlertDialog.Builder(this)
                .setTitle(R.string.no_internet_dialog_title)
                .setMessage(R.string.no_internet_dialog_message)
                .setPositiveButton(R.string.ok, null)
                .show()
    }

    abstract fun hideToolbar()
    abstract fun showToolbar()

    @LayoutRes
    abstract fun layout(): Int

    abstract fun getLoadingView(): View

    override fun showToast(@StringRes message: Int) = showToast(getString(message))
    override fun showToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}