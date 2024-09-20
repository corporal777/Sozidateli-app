package com.example.ui.base

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.R
import com.example.databinding.ActivityMainBinding
import com.example.databinding.LayoutBottomNavBadgeBinding
import com.example.interfaces.BackgroundImageFragment
import com.example.ui.auth.authorization.AuthorizationFragment
import com.example.ui.event.about.AboutEventFragment
import com.example.ui.event.my.MyEventsFragment
import com.example.ui.event.my.schedule.MyScheduleEventsFragment
import com.example.ui.event.registration.EventRegistrationFragment
import com.example.ui.stories.StoriesFragment
import com.example.ui.views.dialogs.CustomProgressDialog
import com.example.ui.views.dialogs.EventAddedToFavoriteDialog
import com.example.util.cancelWindowTransparency
import com.example.util.doEdgeWindow
import com.example.util.setWindowTransparency
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.android.AndroidInjection
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins

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

        RxJavaPlugins.setErrorHandler { e ->
            if (e is UndeliverableException) e.printStackTrace()
            else {
                Thread.currentThread().also { thread ->
                    thread.uncaughtExceptionHandler?.uncaughtException(thread, e)
                }
            }
        }
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return false
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    abstract fun handleIntent(intent: Intent)

    override fun showAddedToFavoriteDialog() {
        EventAddedToFavoriteDialog(0, this)
    }
    override fun showRemovedFromFavoriteDialog() {
        EventAddedToFavoriteDialog(1, this)
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
            hideProgressView()
            hideCustomLoading()
        }
    }

    override fun showCustomProgressDialog() = mProgressDialog.showDialog()
    override fun hideCustomProgressDialog() = mProgressDialog.hideDialog()
    override fun showCustomLoading() {}
    override fun hideCustomLoading() {}

    override fun hideKeyboard() = hideKeyboard(currentFocus)

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

    fun getNavHostFragment(): NavHostFragment {
        return supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
    }

    fun NavHostFragment.primaryNavFragment(): Fragment? {
        return childFragmentManager.primaryNavigationFragment
    }

    fun isCurrentDestination(frag : Int): Boolean {
        return findNavController(R.id.navHostFragment).currentDestination?.id == frag
    }

    fun isPreviousDestination(id : Int) : Boolean {
        val prevId = getNavHostFragment().navController.previousBackStackEntry?.destination?.id
        return prevId == id
    }

    fun setupBackgroundImageFragment(f : Fragment){
        val isLightStatus: Boolean
        val bg: Drawable?
        if (f is BackgroundImageFragment) {
            bg = f.getFragmentBackgroundDrawable()
            isLightStatus = f.isLightStatus
        } else {
            bg = null
            isLightStatus = true
        }
        window.decorView.systemUiVisibility = if (isLightStatus) View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR else 0
        mBinding.root.background = bg
    }

    fun setupBackgroundTransparency(f : Fragment){
        if (f is AboutEventFragment) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            setWindowTransparency()
        }
        else if (f is StoriesFragment) doEdgeWindow()
        else if (f is MyEventsFragment || f is MyScheduleEventsFragment) {
            cancelWindowTransparency()
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        } else {
            cancelWindowTransparency()
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
    }

    abstract fun showProgressView()
    abstract fun hideProgressView()

    abstract fun getLoadingView(): View
    abstract fun getProgressBarLoadingView(): View

    override fun showToast(@StringRes message: Int) = showToast(getString(message))
    override fun showToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}