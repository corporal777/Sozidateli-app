package com.example.ui.base

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.app.R
import com.example.app.databinding.ActivityMainBinding
import com.example.app.databinding.LayoutBottomNavBadgeBinding
import com.example.extensions.statusBarColorValue
import com.example.interfaces.BackgroundImageFragment
import com.example.ui.event.about.AboutEventFragment
import com.example.ui.event.my.MyEventsFragment
import com.example.ui.event.my.schedule.MyScheduleEventsFragment
import com.example.ui.stories.StoriesFragment
import com.example.ui.views.dialogs.CustomProgressDialog
import com.example.ui.views.dialogs.EventAddedToFavoriteDialog
import com.example.util.SYSTEM_UI_LIGHT_STATUS_BAR
import com.example.util.cancelWindowTransparency
import com.example.util.doEdgeWindow
import com.example.util.setWindowTransparency
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import java.net.UnknownHostException

abstract class BaseActivity : MvpAppCompatActivity(), BaseContract.View {

    private var countVisibleLoading = 0
    lateinit var mBinding : ActivityMainBinding
    private lateinit var mProgressDialog : CustomProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mProgressDialog = CustomProgressDialog(this)

        RxJavaPlugins.setErrorHandler { e ->
            if (e is UndeliverableException) e.printStackTrace()
            else if (e is UnknownHostException) e.printStackTrace()
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

    override fun showAddedToFavoriteDialog() = EventAddedToFavoriteDialog(0, this).show()
    override fun showRemovedFromFavoriteDialog() = EventAddedToFavoriteDialog(1, this).show()


    override fun showProgressBarLoading() {
        if (!isFinishing) runOnUiThread {
            countVisibleLoading++
            showProgressView()
        }
    }

    override fun hideProgressBarLoading() {
        if (!isFinishing) runOnUiThread {
            countVisibleLoading--
            if (countVisibleLoading <= 0) {
                countVisibleLoading = 0
                hideProgressView()
            }
        }
    }

    override fun showProgressBarDialogLoading() = mProgressDialog.showDialog()
    override fun hideProgressBarDialogLoading() = mProgressDialog.hideDialog()

    override fun showCustomLoading() {}
    override fun hideCustomLoading() {}

    override fun hideAllLoadingDialogs() {
        if (!isFinishing) runOnUiThread {
            countVisibleLoading = 0
            hideProgressBarLoading()
            hideProgressBarDialogLoading()
            hideCustomLoading()
        }
    }



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
        statusBarColorValue = if (isLightStatus) SYSTEM_UI_LIGHT_STATUS_BAR else 0
        mBinding.root.background = bg
    }


    fun setupBackgroundTransparency(f : Fragment){
        if (f is MyEventsFragment || f is MyScheduleEventsFragment)
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        else window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        if (f is AboutEventFragment) setWindowTransparency { f.setupToolbarTopMargin(it) }
        else if (f is StoriesFragment) doEdgeWindow()
        else cancelWindowTransparency()
    }

    abstract fun showProgressView()
    abstract fun hideProgressView()

    override fun showToast(@StringRes message: Int) = showToast(getString(message))
    override fun showToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}