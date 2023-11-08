package com.example.ui.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import com.example.R
import com.example.databinding.ActivityMainBinding
import com.example.databinding.LayoutBottomNavBadgeBinding
import com.example.ui.views.dialogs.CustomProgressDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.android.AndroidInjection
import io.github.inflationx.viewpump.ViewPumpContextWrapper

abstract class BaseActivity : MvpAppCompatActivity(), BaseContract.View {

    private var countVisibleLoading = 0
    lateinit var mBinding : ActivityMainBinding
    private lateinit var mProgressDialog : CustomProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        getLoadingView().setOnTouchListener { _, _ -> return@setOnTouchListener true }
        mProgressDialog = CustomProgressDialog(this)
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    fun BottomNavigationView.setBadge(tabResId: Int, badgeValue: Int) {
        getOrCreateBadge(this, tabResId)?.let { badge ->
            badge.clBadge.isVisible = badgeValue > 0
            badge.tvBadge.text = if (badgeValue > 99) "99"
            else badgeValue.toString()
        }
    }

    private fun getOrCreateBadge(bottomBar: View, tabResId: Int): LayoutBottomNavBadgeBinding? {
        val parentView = bottomBar.findViewById<ViewGroup>(tabResId)
        var binding: LayoutBottomNavBadgeBinding? = null
        parentView?.let {
            if (parentView.findViewById<ViewGroup>(R.id.clBadge) == null) {
                binding = LayoutBottomNavBadgeBinding.inflate(
                    LayoutInflater.from(parentView.context),
                    parentView,
                    true
                )
            } else {
                val badgeCl = parentView.findViewById<ViewGroup>(R.id.clBadge)
                binding = LayoutBottomNavBadgeBinding.bind(badgeCl)
            }
        }
        return binding
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    abstract fun handleIntent(intent: Intent)

    override fun showEnterAnimation() {
    }

    override fun showEventAddedToFavoriteDialog() {
    }

    override fun showEventRemovedFromFavoriteDialog() {
    }

    override fun showLoadingDialog() {
        if (!isFinishing) runOnUiThread {
            countVisibleLoading++
            showProgressView()
            getLoadingView().visibility = View.VISIBLE
        }
    }

    override fun showProgressBarLoadingDialog() {
        if (!isFinishing) runOnUiThread {
            countVisibleLoading++
            showProgressView()
            getProgressBarLoadingView().visibility = View.VISIBLE
        }
    }

    override fun hideProgressBarLoadingDialog() {
        if (!isFinishing) runOnUiThread {
            countVisibleLoading--
            if (countVisibleLoading <= 0) {
                countVisibleLoading = 0
                getProgressBarLoadingView().visibility = View.GONE
                hideProgressView()
            }

        }
    }

    override fun hideLoadingDialog() {
        if (!isFinishing) runOnUiThread {
            countVisibleLoading--
            if (countVisibleLoading <= 0) {
                countVisibleLoading = 0
                getLoadingView().visibility = View.GONE
                hideProgressView()
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

    override fun showCustomLoading() {
    }

    override fun hideCustomLoading() {
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

    abstract fun showProgressView()
    abstract fun hideProgressView()

    abstract fun getLoadingView(): View
    abstract fun getProgressBarLoadingView(): View

    override fun showToast(@StringRes message: Int) = showToast(getString(message))
    override fun showToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}