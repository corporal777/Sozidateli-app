package com.example.ui.userprofile.base

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.example.R
import com.example.databinding.FragmentUserEditBinding
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.views.toolbar.ToolbarContent
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import onBackPressedCallback

abstract class BaseUserProfileEditFragment : BaseFragment<FragmentUserEditBinding>(), ToolbarFragment {

    val groupAdapter = GroupAdapter<GroupieViewHolder>()

    var onSaveClick: (() -> Unit)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

    override fun layout() = R.layout.fragment_user_edit
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: Int) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
}