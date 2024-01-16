package com.example.ui.gallery.test

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.transition.AutoTransition
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.example.R
import com.example.databinding.FragmentTestBlurBinding
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.views.toolbar.ToolbarContent
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder

class TestBlurFragment : BaseFragment<FragmentTestBlurBinding>(), ToolbarFragment  {

    override fun layout(): Int = R.layout.fragment_test_blur



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
        }
    }

    override val title: CharSequence
        get() = ""
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: Int) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}

}