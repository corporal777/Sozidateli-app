package com.example.ui.userprofile.base

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.example.app.R
import com.example.app.databinding.FragmentUserEditBinding
import com.example.extensions.onBackPressedCallback
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseToolbarFragment
import com.example.ui.base.BaseVBFragment
import com.example.ui.views.toolbar.ToolbarContent
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

abstract class BaseUserProfileEditFragment : BaseToolbarFragment<FragmentUserEditBinding>() {

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

    override fun showCustomLoading() = mBinding.btnSave.showProgressLoading(true)
    override fun hideCustomLoading() = mBinding.btnSave.showProgressLoading(false)

    fun buttonSaveEnabled(enable: Boolean) = mBinding.btnSave.let { it.isEnabled = enable }

    override fun animationType(): AnimType {
        return if (isPreviousDestination(R.id.request_fragment)) AnimType.AXIS else AnimType.NONE
    }
    override fun binding() = FragmentUserEditBinding::class.java
    override fun layout() = R.layout.fragment_user_edit
    override fun scrollingView(): View? = mBinding.recyclerView
}