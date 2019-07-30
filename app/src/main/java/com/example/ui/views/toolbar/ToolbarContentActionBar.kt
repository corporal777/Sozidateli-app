package com.example.ui.views.toolbar

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.SpinnerAdapter
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import java.lang.ref.WeakReference

class ToolbarContentActionBar(
        appCompatActivity: AppCompatActivity,
        private val actionBar: ActionBar
) : ActionBar() {

    private val weakActivity = WeakReference(appCompatActivity)

    private val customView: ToolbarContentView = actionBar.customView as ToolbarContentView

    private val navigationIcon by lazy {
        ToolbarBackButton(appCompatActivity).apply {
            setOnClickListener { weakActivity.get()?.onSupportNavigateUp() }
        }
    }

    override fun setDisplayHomeAsUpEnabled(showHomeAsUp: Boolean) {
        if (showHomeAsUp) addLeftView(navigationIcon, 0)
        else (removeLeftView(navigationIcon))
    }

    override fun show() = actionBar.show()
    override fun isShowing() = actionBar.isShowing
    override fun hide() = actionBar.hide()

    override fun getDisplayOptions() = actionBar.displayOptions

    override fun setTitle(title: CharSequence?) = customView.getTitleView { text = title }

    override fun setTitle(resId: Int) = customView.getTitleView { text = title }

    fun addLeftView(view: View, position: Int = 0) {
        customView.getLeftViewContainer {
            if (view == navigationIcon && navigationIcon.parent == this) return@getLeftViewContainer
            if (position == 0 && view != navigationIcon && navigationIcon.parent == this) {
                addView(view, 1)
            } else {
                addView(view, position)
            }
        }
    }

    fun removeLeftView(view: View) = customView.getLeftViewContainer { removeView(view) }
    fun removeLeftView(position: Int) = customView.getLeftViewContainer { removeViewAt(position) }
    fun removeAllLeftViews() = customView.getLeftViewContainer {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child != navigationIcon) removeView(child)
        }
    }

    fun addRightView(view: View, position: Int = 0) = customView.getRightViewContainer { addView(view, position) }
    fun removeRightView(view: View) = customView.getRightViewContainer { removeView(view) }
    fun removeRightView(position: Int) = customView.getRightViewContainer { removeViewAt(position) }
    fun removeAllRightViews() = customView.getRightViewContainer { removeAllViews() }

    fun setOnToolbarClickListener(listener: OnToolbarClickListener?) {
        if (listener == null) customView.setOnClickListener(null)
        else customView.setOnClickListener { listener() }
    }

    override fun setListNavigationCallbacks(adapter: SpinnerAdapter?, callback: OnNavigationListener?) {
        throw UnsupportedOperationException("Do not supported by ToolbarContentActionBar")
    }

    override fun setSelectedNavigationItem(position: Int) {
        throw UnsupportedOperationException("Do not supported by ToolbarContentActionBar")
    }

    override fun setDisplayOptions(options: Int) {
        throw UnsupportedOperationException("Do not supported by ToolbarContentActionBar")
    }

    override fun setDisplayOptions(options: Int, mask: Int) {
        throw UnsupportedOperationException("Do not supported by ToolbarContentActionBar")
    }

    override fun removeOnMenuVisibilityListener(listener: OnMenuVisibilityListener?) {
        throw UnsupportedOperationException("Do not supported by ToolbarContentActionBar")
    }

    override fun getCustomView(): View {
        throw UnsupportedOperationException("Do not supported by ToolbarContentActionBar")
    }

    override fun setCustomView(view: View?) {
        throw UnsupportedOperationException("Do not supported by ToolbarContentActionBar")
    }

    override fun setCustomView(view: View?, layoutParams: LayoutParams?) {
        throw UnsupportedOperationException("Do not supported by ToolbarContentActionBar")
    }

    override fun setCustomView(resId: Int) {
        throw UnsupportedOperationException("Do not supported by ToolbarContentActionBar")
    }

    override fun getSubtitle(): CharSequence? {
        throw UnsupportedOperationException("Do not supported by ToolbarContentActionBar")
    }


    override fun getTitle(): CharSequence? {
        throw UnsupportedOperationException("Do not supported by ToolbarContentActionBar")
    }

    override fun setDisplayShowHomeEnabled(showHome: Boolean) {
        throw UnsupportedOperationException("Do not supported by ToolbarContentActionBar")
    }

    override fun setDisplayUseLogoEnabled(useLogo: Boolean) {
        throw UnsupportedOperationException("Do not supported by ToolbarContentActionBar")
    }

    override fun getTabCount(): Int {
        throw UnsupportedOperationException("Do not supported by ToolbarContentActionBar")
    }

    override fun setLogo(resId: Int) {
        throw UnsupportedOperationException("Do not supported by ToolbarContentActionBar")
    }

    override fun setLogo(logo: Drawable?) {
        throw UnsupportedOperationException("Do not supported by ToolbarContentActionBar")
    }

    override fun getHeight(): Int {
        throw UnsupportedOperationException("Do not supported by ToolbarContentActionBar")
    }


    override fun newTab(): Tab {
        throw UnsupportedOperationException("Do not supported by ToolbarContentActionBar")
    }

    override fun setBackgroundDrawable(d: Drawable?) {
        throw UnsupportedOperationException("Do not supported by ToolbarContentActionBar")
    }

    override fun setNavigationMode(mode: Int) {
        throw UnsupportedOperationException("Do not supported by ToolbarContentActionBar")
    }

    override fun removeTabAt(position: Int) {
        throw UnsupportedOperationException("Do not supported by ToolbarContentActionBar")
    }

    override fun getTabAt(index: Int): Tab {
        throw UnsupportedOperationException("Do not supported by ToolbarContentActionBar")
    }

    override fun addTab(tab: Tab?) {
        throw UnsupportedOperationException("Do not supported by ToolbarContentActionBar")
    }

    override fun addTab(tab: Tab?, setSelected: Boolean) {
        throw UnsupportedOperationException("Do not supported by ToolbarContentActionBar")
    }

    override fun addTab(tab: Tab?, position: Int) {
        throw UnsupportedOperationException("Do not supported by ToolbarContentActionBar")
    }

    override fun addTab(tab: Tab?, position: Int, setSelected: Boolean) {
        throw UnsupportedOperationException("Do not supported by ToolbarContentActionBar")
    }

    override fun setIcon(resId: Int) {
        throw UnsupportedOperationException("Do not supported by ToolbarContentActionBar")
    }

    override fun setIcon(icon: Drawable?) {
        throw UnsupportedOperationException("Do not supported by ToolbarContentActionBar")
    }

    override fun removeAllTabs() {
        throw UnsupportedOperationException("Do not supported by ToolbarContentActionBar")
    }

    override fun getNavigationItemCount(): Int {
        throw UnsupportedOperationException("Do not supported by ToolbarContentActionBar")
    }

    override fun addOnMenuVisibilityListener(listener: OnMenuVisibilityListener?) {
        throw UnsupportedOperationException("Do not supported by ToolbarContentActionBar")
    }

    override fun removeTab(tab: Tab?) {
        throw UnsupportedOperationException("Do not supported by ToolbarContentActionBar")
    }

    override fun setSubtitle(subtitle: CharSequence?) {
        throw UnsupportedOperationException("Do not supported by ToolbarContentActionBar")
    }

    override fun setSubtitle(resId: Int) {
        throw UnsupportedOperationException("Do not supported by ToolbarContentActionBar")
    }

    override fun setDisplayShowTitleEnabled(showTitle: Boolean) {
        throw UnsupportedOperationException("Do not supported by ToolbarContentActionBar")
    }

    override fun getSelectedTab(): Tab? {
        throw UnsupportedOperationException("Do not supported by ToolbarContentActionBar")
    }

    override fun selectTab(tab: Tab?) {
        throw UnsupportedOperationException("Do not supported by ToolbarContentActionBar")
    }

    override fun getNavigationMode(): Int {
        throw UnsupportedOperationException("Do not supported by ToolbarContentActionBar")
    }

    override fun setDisplayShowCustomEnabled(showCustom: Boolean) {
        throw UnsupportedOperationException("Do not supported by ToolbarContentActionBar")
    }

    override fun getSelectedNavigationIndex(): Int {
        throw UnsupportedOperationException("Do not supported by ToolbarContentActionBar")
    }

}

typealias OnToolbarClickListener = () -> Unit