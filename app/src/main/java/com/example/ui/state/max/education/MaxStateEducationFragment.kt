package com.example.ui.state.max.education

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.UserDetail
import com.example.databinding.FragmentMaxStateEducationBinding
import com.example.holders.ProfileDataEducationEditGroupNew
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragmentNew
import com.example.ui.state.max.work.MaxStateWorkFragmentArgs
import com.example.ui.userprofile.read.settings.confirm_phone_email.ConfirmEmailPhoneFragment
import com.example.ui.views.AddPhoneEmailDialog
import com.example.ui.views.ConfirmPhoneDialog
import com.example.ui.views.RegisterDataType
import com.example.ui.views.dialogs_new.MessageDialogWithBrownButton
import com.example.ui.views.toolbar.ToolbarContent
import com.example.ui.views.toolbar.ToolbarIconView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import onScrolled
import javax.inject.Inject
import javax.inject.Provider

class MaxStateEducationFragment : BaseFragmentNew<FragmentMaxStateEducationBinding>(),
    MaxStateEducationContract.View, ToolbarFragment {

    override fun layout(): Int = R.layout.fragment_max_state_education
    private lateinit var dialog: AddPhoneEmailDialog

    @InjectPresenter
    lateinit var presenter: MaxStateEducationPresenter

    @Inject
    lateinit var presenterProvider: Provider<MaxStateEducationPresenter>

    @ProvidePresenter
    fun providePresenter(): MaxStateEducationPresenter = presenterProvider.get().apply {
        screen = MaxStateWorkFragmentArgs.fromBundle(requireArguments()).screen
    }

    private val adapter = GroupAdapter<GroupieViewHolder>()

    private var onSaveClick: (() -> Unit)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.recyclerView.apply {
            adapter = this@MaxStateEducationFragment.adapter
        }

        mBinding.btnSave.apply {
            isEnabled = false
            setOnClickListener { onSaveClick?.invoke() }
        }
    }

    override fun setEducationData(user: UserDetail) {
        val academicDegree =
            if (user.binds?.academicDegree?.size == 1 && user.binds?.academicDegree?.get(0)?.degree == null)
                null else user.binds?.academicDegree
        val dataItem = ProfileDataEducationEditGroupNew(
            requireContext(),
            user.birthday,
            user.educationLevel,
            user.educationLevelList ?: emptyList(),
            user.academicDegrees ?: emptyList(),
            user.speciality ?: emptyList(),
            user.binds?.education ?: emptyList(),
            academicDegree ?: emptyList()
        ) { isEnable -> mBinding.btnSave.isEnabled = isEnable }
        adapter.update(listOf(dataItem))

        onSaveClick = {
            if (dataItem.checkDataValid()) {
                presenter.onSaveEducationClick(
                    dataItem.getEducationLevelToSave(),
                    dataItem.getEducationsToSave(),
                    dataItem.getDegreeToSave()
                )
            }
        }
    }

    override fun goToNext() {
        if (presenter.getEmail()?.value != null && presenter.getEmail()?.isConfirmed != null) {
            maxActionWithSuccess()
        } else {
            dialog = AddPhoneEmailDialog(requireActivity(), RegisterDataType.EMAIL)
                .setSelectCallback {
                    presenter.checkEmailIsUnique(it.value)
                    dialog.hideDialog()
                }.setNegativeClickCallback { maxActions() }
        }
    }

    private fun maxActionWithSuccess() {
        MessageDialogWithBrownButton(requireContext(), getString(R.string.you_got_max_state), false)
            .setSelectCallback {
                maxActions()
            }
    }

    private fun maxActions() {
        when (presenter.screen) {
            1 -> findNavController().popBackStack(R.id.profile_fragment, false)
            2 -> findNavController().popBackStack(R.id.userStateFragment, false)
        }
    }

    override fun showChangeEmailComplete(email: String) {
        maxActionWithSuccess()
    }

    override fun showEmailIsNotUnique(email: String) {
        ConfirmPhoneDialog(
            requireContext(), getString(R.string.confirm_email_text, email),
            getString(R.string.revoke), getString(R.string.confirm_phone_positive)
        )
            .setSelectCallback {
                if (it) {
                    presenter.onShowEmailConfirm(email)
                }
            }
    }

    override fun showEmailConfirmation(email: String) {
        val confirmEmail = ConfirmEmailPhoneFragment(email)
        confirmEmail.show(requireActivity().supportFragmentManager, "max_state_confirm_email")
        confirmEmail.setConfirmCallback {
            showChangeEmailComplete(email)
        }
    }

    override fun showUpdateError(message: String?) {
        val title = getString(R.string.profile_edit_request_error)
        Toast.makeText(requireContext(), message?.let { "$title: $it" }
            ?: title, Toast.LENGTH_SHORT).show()
    }


    override fun setClickClose(type: Int) {
        when (presenter.screen) {
            1 -> findNavController().popBackStack(R.id.profile_fragment, false)
            2 -> findNavController().popBackStack(R.id.userStateFragment, false)
            else -> navigateUp()
        }
    }


    override val title: CharSequence by lazy { getString(R.string.profile_title_education) }
    override fun actionIconContainer(view: ViewGroup) {
        view.apply {
            addView(ToolbarIconView(context, 40).apply {
                setImageAsIcon(R.drawable.ic_close_new)
                setOnClickListener { presenter.onClickClose() }
            })
        }
    }

    override fun scrollValue(scroll: (value: Int) -> Unit) {
        mBinding.recyclerView.apply {
            scroll.invoke(this.computeVerticalScrollOffset())
            onScrolled { _, _ ->
                scroll.invoke(this.computeVerticalScrollOffset())
            }
        }
    }

    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
}