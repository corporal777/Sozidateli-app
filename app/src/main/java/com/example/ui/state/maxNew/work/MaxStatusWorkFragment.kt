package com.example.ui.state.maxNew.work

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.UserDetail
import com.example.extensions.updateGroup
import com.example.holders.ProfileDataWorkEditGroup
import com.example.ui.state.maxNew.base.BaseMaxStateFragment
import com.example.ui.userprofile.read.settings.confirm_phone_email.ConfirmEmailPhoneFragment
import com.example.ui.views.AddPhoneEmailDialog
import com.example.ui.views.ConfirmPhoneDialog
import com.example.ui.views.RegisterDataType
import javax.inject.Inject
import javax.inject.Provider

class MaxStatusWorkFragment : BaseMaxStateFragment<MaxStatusWorkPresenter>(),
    MaxStatusWorkContract.View {

    override val title: CharSequence by lazy { getString(R.string.profile_work_experience) }

    private lateinit var dialog: AddPhoneEmailDialog

    @InjectPresenter
    override lateinit var presenter: MaxStatusWorkPresenter

    @Inject
    lateinit var presenterProvider: Provider<MaxStatusWorkPresenter>

    @ProvidePresenter
    fun providePresenter(): MaxStatusWorkPresenter = presenterProvider.get().apply {
        screen = MaxStatusWorkFragmentArgs.fromBundle(requireArguments()).screen
    }


    override fun setWorkData(user: UserDetail) {
        val work = user.binds?.workExperience
        val dataItem = ProfileDataWorkEditGroup(
            requireContext(),
            user.birthday,
            work,
            { },
            { isEnable -> buttonNextEnabled(isEnable) })

        contentSection.updateGroup(dataItem)
        onSaveClick = {
            if (dataItem.checkDataValid()) {
                presenter.onSaveWorkClick(dataItem.getDataToSave())
            }
        }
    }

    override fun showAddEmailDialog() {
        dialog = AddPhoneEmailDialog(requireContext(), RegisterDataType.EMAIL)
            .setSelectCallback {
                presenter.checkEmailIsUnique(it.value)
            }.setNegativeClickCallback { presenter.onClickClose() }
    }

    override fun hideAddEmailDialog() {
        dialog.hideDialog()
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
            presenter.checkNextScreen()
        }
    }
}