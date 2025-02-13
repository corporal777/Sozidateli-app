package com.example.ui.state.maxNew.base

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.core.os.bundleOf
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.example.app.R
import com.example.app.databinding.FragmentMaxStateInfoBinding
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseVBFragment
import com.example.ui.state.maxNew.MaxStateScreenType
import com.example.ui.state.maxNew.contacts.MaxStatusContactsFragmentArgs
import com.example.ui.state.maxNew.education.MaxStatusEducationFragmentArgs
import com.example.ui.state.maxNew.interests.MaxStatusInterestsFragmentArgs
import com.example.ui.state.maxNew.work.MaxStatusWorkFragmentArgs
import com.example.ui.views.dialogs.AddPhoneEmailDialog
import com.example.ui.views.dialogs.ContactsType
import com.example.ui.views.dialogs.DefaultAlertDialog
import com.example.ui.views.toolbar.ToolbarContent
import com.example.ui.views.toolbar.ToolbarIconView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section

abstract class BaseMaxStateFragment<P : BaseMaxStateContract.Presenter> :
    BaseVBFragment<FragmentMaxStateInfoBinding>(),
    BaseMaxStateContract.View, ToolbarFragment {

    abstract var presenter: P

    private lateinit var dialog: AddPhoneEmailDialog

    val contentSection = Section()
    private val groupAdapter = GroupAdapter<GroupieViewHolder>().apply {
        add(contentSection)
    }

    var onSaveClick: (() -> Unit)? = null

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            recyclerView.adapter = groupAdapter
            btnSave.setOnClickListener { onSaveClick?.invoke() }
        }
    }

    override fun buttonNextEnabled(enabled: Boolean) {
        mBinding.btnSave.isEnabled = enabled
    }


    override fun goToNextScreen(screenType: MaxStateScreenType) {
        when (screenType) {
            MaxStateScreenType.BASE -> {
                findNavController().navigate(
                    R.id.maxStatusContactsFragment,
                    MaxStatusContactsFragmentArgs.Builder().setScreen(2).build().toBundle()
                )
            }

            MaxStateScreenType.INTERESTS -> {
                findNavController().navigate(
                    R.id.maxStatusInterestsFragment,
                    MaxStatusInterestsFragmentArgs.Builder().setScreen(2).build().toBundle()
                )
            }

            MaxStateScreenType.EDUCATION -> {
                findNavController().navigate(
                    R.id.maxStatusEducationFragment,
                    MaxStatusEducationFragmentArgs.Builder().setScreen(2).build().toBundle()
                )
            }

            MaxStateScreenType.WORK -> {
                findNavController().navigate(
                    R.id.maxStatusWorkFragment,
                    MaxStatusWorkFragmentArgs.Builder().setScreen(2).build().toBundle()
                )
            }

            MaxStateScreenType.DONE -> presenter.checkUserEmail()
        }
    }

    override fun showMaxStateDone(screen: Int) {
        DefaultAlertDialog(
            requireContext(),
            null,
            getString(R.string.you_got_max_state),
            withCancel = false
        ).setSelectCallback {
            when (screen) {
                1 -> findNavController().popBackStack(R.id.profile_fragment, false)
                2 -> findNavController().popBackStack(R.id.userStateFragment, false)
            }
        }
    }

    override fun showAddEmailDialog() {
        dialog = AddPhoneEmailDialog(requireContext(), ContactsType.EMAIL)
            .setSelectEmailCallback { presenter.checkEmailIsUnique(true, it) }
            .setNegativeClickCallback { presenter.onClickClose() }
    }

    override fun hideAddEmailDialog() = dialog.hideDialog()

    override fun showEmailIsNotUnique(email: String) {
        DefaultAlertDialog(
            requireContext(),
            null,
            getString(R.string.confirm_email_text, email),
            getString(R.string.confirm_phone_positive),
            getString(R.string.event_register_no_form_negative)
        ).setSelectCallback { presenter.checkEmailIsUnique(false, email) }
    }

    override fun showEmailConfirmation(email: String) {
        findNavController().navigate(
            R.id.emailCodeConfirmFragment,
            bundleOf("email" to email, "fromRegister" to false),
        )
        setFragmentResultListener("confirm") { _, bundle ->
            val emailConfirm = bundle.getString("email")
            if (!emailConfirm.isNullOrEmpty()) presenter.onShowMaxStateDone()
            clearFragmentResultListener("confirm")
        }
    }

    override fun setClickClose(type: Int) {
        when (type) {
            1 -> findNavController().popBackStack(R.id.profile_fragment, false)
            2 -> findNavController().popBackStack(R.id.userStateFragment, false)
            else -> navigateUp()
        }
    }

    override fun showUpdateError(message: String?) {
        val title = getString(R.string.profile_edit_request_error)
        Toast.makeText(requireContext(), message?.let { "$title: $it" }
            ?: title, Toast.LENGTH_SHORT).show()
    }

    fun getAdapter() = groupAdapter

    override fun showCustomLoading() {
        mBinding.apply {
            btnSave.showProgressLoading(true)
        }
    }

    override fun hideCustomLoading() {
        mBinding.apply {
            btnSave.showProgressLoading(false)
        }
    }

    override fun actionIconContainer(view: ViewGroup) {
        view.apply {
            addView(ToolbarIconView(context, 40).apply {
                setImageAsIcon(R.drawable.ic_close_new)
                setOnClickListener { presenter.onClickClose() }
            })
        }
    }

    override fun scrollValue(scroll: Int) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
    override fun binding() = FragmentMaxStateInfoBinding::class.java
    override fun layout(): Int = R.layout.fragment_max_state_info
}