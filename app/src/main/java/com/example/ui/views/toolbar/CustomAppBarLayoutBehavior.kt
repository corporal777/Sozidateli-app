package com.example.ui.views.toolbar

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ScrollView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.allViews
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.R
import com.example.ui.main.MainActivity
import com.google.android.material.appbar.AppBarLayout
import onScrolled

class CustomAppBarLayoutBehavior : AppBarLayout.ScrollingViewBehavior {

    constructor() : super()
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    private var onScrollChange: (value: Int) -> Unit = {}

    @SuppressLint("RestrictedApi")
    override fun onLayoutChild(
        parent: CoordinatorLayout,
        child: View,
        layoutDirection: Int
    ): Boolean {
        try {
            if (child is FragmentContainerView) {
                val navHost = (parent.context as MainActivity).getNavHostFragment()
                val fr = navHost.childFragmentManager.fragments.firstOrNull()
                if (fr == null || fr.view == null) return super.onLayoutChild(parent, child, layoutDirection)

                if (fr.view is NestedScrollView || fr.view is RecyclerView) findView(fr.view)
                else {
                    val scrollView = fr.view?.allViews?.find { x -> x is RecyclerView || x is NestedScrollView || x is ScrollView }
                    if (scrollView != null) findView(scrollView)
                    else onScrollChange.invoke(0)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            onScrollChange.invoke(0)
        }

        return super.onLayoutChild(parent, child, layoutDirection)
    }


    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        return if (dependency is AppBarLayout && dependency.isVisible) {
            super.layoutDependsOn(parent, child, dependency)
        } else false
    }


    @SuppressLint("RestrictedApi")
    private fun findView(view: View?) {
        if (view != null) {
            if (view is NestedScrollView) {
                onScrollChange.invoke(view.computeVerticalScrollOffset())
                view.onScrolled { _, _, _, _ ->
                    onScrollChange.invoke(view.computeVerticalScrollOffset())
                }
            } else if (view is RecyclerView) {
                onScrollChange.invoke(view.computeVerticalScrollOffset())
                view.onScrolled { _, _ ->
                    onScrollChange.invoke(view.computeVerticalScrollOffset())
                }
            }
            else onScrollChange.invoke(0)
        }
    }

    fun setScrollChangeCallback(scroll: (value: Int) -> Unit): CustomAppBarLayoutBehavior {
        onScrollChange = scroll
        return this
    }

    fun getScrollChange() = onScrollChange
}