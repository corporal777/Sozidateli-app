package com.example.ui.userprofile.base

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.example.app.R
import com.example.app.databinding.FragmentUserEditBinding
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.views.toolbar.ToolbarContent
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.example.extensions.onBackPressedCallback

abstract class BaseUserProfileEditFragment : BaseFragment<FragmentUserEditBinding>(), ToolbarFragment {

    val groupAdapter = GroupAdapter<GroupieViewHolder>()

    var onSaveClick: (() -> Unit)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (animationType() != AnimType.NONE) startPostponedEnterTransition()
        onBackPressedCallback(true) {
            hideKeyboard()
            navigateUp()
        }
        mBinding.recyclerView.apply {
            adapter = groupAdapter
        }
        mBinding.btnSave.setOnClickListener { onSaveClick?.invoke() }
    }
    override fun showCustomLoading() {
        mBinding.apply { btnSave.showProgressLoading(true) }
    }

    override fun hideCustomLoading() {
        mBinding.apply { btnSave.showProgressLoading(false) }
    }

    fun buttonSaveEnabled(enable: Boolean){
        mBinding.btnSave.isEnabled = enable
    }

    override fun animationType(): AnimType {
        return if (isPreviousDestination(R.id.request_fragment)) AnimType.AXIS else AnimType.NONE
    }
    override fun layout() = R.layout.fragment_user_edit
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: Int) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
}