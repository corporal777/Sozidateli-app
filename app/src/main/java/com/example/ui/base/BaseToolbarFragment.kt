package com.example.ui.base

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.common.extensions.onScrolled
import com.example.interfaces.ToolbarFragment
import com.example.ui.main.MainActivity
import com.example.ui.views.loading.CustomCircleLoadingButton
import com.example.ui.views.toolbar.ToolbarContent
import com.example.ui.views.toolbar.ToolbarIconView
import kotlin.math.abs

abstract class BaseToolbarFragment<VB : ViewBinding> : BaseVBFragment<VB>(), ToolbarFragment {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val scrollView = scrollingView()
        when (scrollView) {
            is NestedScrollView -> {
                setActivityAppBarElevation(scrollView.computeVerticalScrollOffset())
                scrollView.onScrolled { _, _, _, _ ->
                    setActivityAppBarElevation(scrollView.computeVerticalScrollOffset())
                }
            }

            is RecyclerView -> {
                setActivityAppBarElevation(scrollView.computeVerticalScrollOffset())
                scrollView.onScrolled { _, _ ->
                    setActivityAppBarElevation(scrollView.computeVerticalScrollOffset())
                }
            }

            else -> setActivityAppBarElevation(0)
        }
    }

    private fun setActivityAppBarElevation(value: Int) {
        try {
            val offset = abs(value / 10f)
            (requireActivity() as MainActivity).setAppBarElevation(offset)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    protected fun createIconView(icon : Int, enabled : Boolean, onClick : () -> Unit): ToolbarIconView {
        return ToolbarIconView(requireContext()).apply {
            isEnabled = enabled
            setImageAsIcon(icon)
            setOnClickListener { onClick.invoke() }
        }
    }

    protected fun createButtonView(text : Int, visible : Boolean, onClick : () -> Unit): CustomCircleLoadingButton {
        return CustomCircleLoadingButton(requireContext()).apply {
            buttonText = requireContext().getString(text)
            isVisible = visible
            setOnClickListener { onClick() }
        }
    }

    override val title: CharSequence = ""
    override fun actionIconContainer(view: ViewGroup) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
    override fun scrollValue(scroll: Int) {}
    open fun scrollingView(): View? = null
}