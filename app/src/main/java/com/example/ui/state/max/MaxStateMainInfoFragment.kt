package com.example.ui.state.max

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.UserDetail
import com.example.databinding.FragmentMaxStateInfoBinding
import com.example.extensions.findItemBy
import com.example.extensions.showChangeEmailCompleteDialog
import com.example.extensions.showChangeEmailDialog
import com.example.holders.MaxStateMainInfoEditItem
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragmentNew
import com.example.ui.views.InfoDialog
import com.example.ui.views.dialogs_new.MessageDialogWithBrownButton
import com.example.ui.views.toolbar.ToolbarContent
import com.example.ui.views.toolbar.ToolbarIconView
import com.example.util.PHONE_PERSONAL
import com.example.util.PHONE_WORK
import com.example.util.Utils
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import onScrolled
import javax.inject.Inject
import javax.inject.Provider

class MaxStateMainInfoFragment : BaseFragmentNew<FragmentMaxStateInfoBinding>(),
    MaxStateMainInfoContract.View, ToolbarFragment {

    private var isOtherInfoValid = false
    private var canUpdateFields = true

    override fun layout(): Int = R.layout.fragment_max_state_info

    @InjectPresenter
    lateinit var presenter: MaxStateMainInfoPresenter

    @Inject
    lateinit var presenterProvider: Provider<MaxStateMainInfoPresenter>

    @ProvidePresenter
    fun providePresenter(): MaxStateMainInfoPresenter = presenterProvider.get().apply {
        screen = MaxStateMainInfoFragmentArgs.fromBundle(requireArguments()).screen
    }

    private var onSaveClick: (() -> Unit)? = null

    private val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.recyclerView.apply {
            adapter = this@MaxStateMainInfoFragment.adapter
        }

        mBinding.btnSave.setOnClickListener { onSaveClick?.invoke() }
        buttonNextEnabled(false)
    }

    override fun setPersonalData(user: UserDetail) {
        if (!isGoToNextStep(user)) {
            val userPhone = user.phone?.firstOrNull { it.type == PHONE_PERSONAL }
            val workPhone = user.phone?.firstOrNull { it.type == PHONE_WORK }
            val dataItem = if (canUpdateFields) MaxStateMainInfoEditItem(
                1,
                requireContext(),
                userPhone,
                workPhone,
                user.contactInformation.socialLinks,
                user.contactInformation.site,
                user.notes,
                user.image,
                user.contactInformation.emails ?: emptyList(),
                { showWhyUserShouldAddDataToNotesField() }, {
                    isOtherInfoValid = it
                    buttonNextEnabled(it)
                }
            ) else adapter.findItemBy<GroupieViewHolder, MaxStateMainInfoEditItem> { true }

            adapter.update(listOf(dataItem))
            onSaveClick = {
                if (dataItem?.workPhoneIsValid() == true) {
                    mBinding.recyclerView.requestFocus()
                    val dataToSave = dataItem.getDataToSave() as MutableMap
                    presenter.updateFiles(dataToSave)
                } else {
                    dataItem?.notValidWorkPhoneError()
                }
            }
        } else {
            if (!presenter.isUpdatePhoto)
                findNavController().navigate(
                    R.id.baseStateInterestsFragment,
                    bundleOf("screen" to presenter.screen)
                )
            presenter.isUpdatePhoto = false
        }
        canUpdateFields = false
    }

    private fun buttonNextEnabled(enabled: Boolean) {
        mBinding.btnSave.isEnabled = isOtherInfoValid
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

    override fun goToNext() {

        when (Utils.maxStateScreen(presenter.getUserData())) {
            MaxStateScreenType.WORK ->
                findNavController().navigate(
                    MaxStateMainInfoFragmentDirections.actionMaxStateMainInfoFragmentToMaxStateWorkFragment()
                        .setScreen(presenter.screen)
                )
            MaxStateScreenType.EDUCATION ->
                findNavController().navigate(
                    R.id.maxStateEducationFragment,
                    bundleOf("screen" to presenter.screen)
                )
            MaxStateScreenType.DONE -> MessageDialogWithBrownButton(
                requireContext(),
                resources.getString(R.string.you_got_max_state)
            )
                .setSelectCallback {
                    when (presenter.screen) {
                        1 -> findNavController().popBackStack(R.id.profile_fragment, false)
                        2 -> findNavController().popBackStack(R.id.userStateFragment, false)
                    }
                }
            else ->
                findNavController().navigate(
                    R.id.baseStateInterestsFragment,
                    bundleOf("screen" to presenter.screen)
                )
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

    override fun showChangeEmail() = showChangeEmailDialog(presenter::onChangeEmailConfirm)

    override fun showChangeEmailComplete(email: String) = showChangeEmailCompleteDialog(email)

    override val title: CharSequence by lazy { getString(R.string.user_profile_contacts) }
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