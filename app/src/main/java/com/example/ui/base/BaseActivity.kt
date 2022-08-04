package com.example.ui.base

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import com.example.databinding.ActivityMainBinding
import com.example.ui.views.dialogs_new.CustomProgressDialog
import dagger.android.AndroidInjection
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_main.view.*

abstract class BaseActivity : MvpAppCompatActivity(), BaseContract.View {

    private var countVisibleLoading = 0
    lateinit var mBinding : ActivityMainBinding
    private lateinit var mProgressDialog : CustomProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        //setContentView(layout())
        setContentView(mBinding.root)
        getLoadingView().setOnTouchListener { _, _ -> return@setOnTouchListener true }
        mProgressDialog = CustomProgressDialog(this)
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    override fun showLoadingDialog() {
        if (!isFinishing) runOnUiThread {
            countVisibleLoading++
            addProgressView()
            getLoadingView().visibility = View.VISIBLE
        }
    }

    override fun enableBackClick() {
        enableBackClickListener()
    }

    override fun disableBackClick() {
        disableBackClickListener()
    }


    override fun showProgressBarLoadingDialog() {
        if (!isFinishing) runOnUiThread {
            countVisibleLoading++
            addProgressView()
            getProgressBarLoadingView().visibility = View.VISIBLE
        }
    }

    override fun hideProgressBarLoadingDialog() {
        if (!isFinishing) runOnUiThread {
            countVisibleLoading--
            if (countVisibleLoading <= 0) {
                countVisibleLoading = 0
                getProgressBarLoadingView().visibility = View.GONE
                removeProgressView()
            }

        }
    }

    override fun hideLoadingDialog() {
        if (!isFinishing) runOnUiThread {
            countVisibleLoading--
            if (countVisibleLoading <= 0) {
                countVisibleLoading = 0
                getLoadingView().visibility = View.GONE
                removeProgressView()
            }

        }
    }

    override fun hideAllLoadingDialogs() {
        if (!isFinishing) runOnUiThread {
            countVisibleLoading = 0
            getLoadingView().visibility = View.GONE
        }
    }

    override fun showCustomProgressDialog() {
        mProgressDialog.showDialog()
    }

    override fun hideCustomProgressDialog() {
        mProgressDialog.hideDialog()
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

    abstract fun hideToolbar()
    abstract fun showToolbar()

    @LayoutRes
    abstract fun layout(): Int

    abstract fun addProgressView()
    abstract fun removeProgressView()

    abstract fun getLoadingView(): View
    abstract fun getProgressBarLoadingView(): View

    abstract fun enableBackClickListener()
    abstract fun disableBackClickListener()

    override fun showToast(@StringRes message: Int) = showToast(getString(message))
    override fun showToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}