package com.example.ui.state.maxNew.mainInfo

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.UserDetail
import com.example.extensions.findItemBy
import com.example.extensions.updateItem
import com.example.holders.MaxStateContactsEditItem
import com.example.ui.state.maxNew.base.BaseMaxStateFragment
import com.example.ui.views.InfoDialog
import com.example.util.PHONE_PERSONAL
import com.example.util.PHONE_WORK
import javax.inject.Inject
import javax.inject.Provider

class MaxStatusContactsFragment : BaseMaxStateFragment<MaxStatusContactsPresenter>(),
    MaxStatusContactsContract.View {

    override val title: CharSequence by lazy { getString(R.string.user_profile_contacts) }
    private var isOtherInfoValid = false

    @InjectPresenter
    override lateinit var presenter: MaxStatusContactsPresenter

    @Inject
    lateinit var presenterProvider: Provider<MaxStatusContactsPresenter>

    @ProvidePresenter
    fun providePresenter(): MaxStatusContactsPresenter = presenterProvider.get().apply {
        screen = MaxStatusContactsFragmentArgs.fromBundle(requireArguments()).screen
    }

    override fun setPersonalData(user: UserDetail) {
        if (!isGoToNextStep(user)) {
            contentSection.updateItem(MaxStateContactsEditItem(
                1,
                requireContext(),
                user.phone?.firstOrNull { it.type == PHONE_PERSONAL },
                user.phone?.firstOrNull { it.type == PHONE_WORK },
                user.contactInformation.socialLinks,
                user.contactInformation.site,
                user.notes,
                user.contactInformation.emails,
                { showWhyUserShouldAddDataToNotesField() }, {
                    isOtherInfoValid = it
                    buttonNextEnabled(it)
                }
            ))
            onSaveClick = { saveData() }
        }
    }

    private fun saveData() {
        val item = contentSection.findItemBy<MaxStateContactsEditItem> { true }
        if (item != null) {
            if (item.workPhoneIsValid()) {
                mBinding.recyclerView.requestFocus()
                presenter.saveContactsClick(item.getDataToSave())
            } else item.notValidWorkPhoneError()
        }
    }

    private fun isGoToNextStep(user: UserDetail): Boolean {
        var isGoToNex = true
        if (user.binds?.recommendationFile?.isEmpty() == true) isGoToNex = false
        if (user.phone?.firstOrNull { it.type == PHONE_WORK }?.value == null) isGoToNex = false
        if (user.contactInformation.socialLinks?.values?.isEmpty() == true) isGoToNex = false
        if (user.contactInformation.site?.values?.isEmpty() == true) isGoToNex = false
        if (user.email?.value.isNullOrEmpty()) isGoToNex = false
        if (user.notes?.value.isNullOrEmpty()) isGoToNex = false
        return isGoToNex
    }

    private fun showWhyUserShouldAddDataToNotesField() {
        InfoDialog(
            requireContext(),
            getString(R.string.profile_edit_additional_notes_data),
            requireActivity()
        )
            .setSelectCallback { }
    }
}