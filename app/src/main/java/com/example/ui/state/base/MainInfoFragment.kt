package com.example.ui.state.base

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.R
import com.example.data.models.FieldDetails
import com.example.data.models.ImageModel
import com.example.data.models.UserDetail
import com.example.databinding.FragmentMainInfoBinding
import com.example.extensions.findItemBy
import com.example.extensions.updateItem
import com.example.holders.MainInfoEditItem
import com.example.holders.PlaceholderItem
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.gallery.GalleryBottomSheet
import com.example.ui.state.UserState
import com.example.ui.state.maxNew.MaxStateScreenType
import com.example.ui.userprofile.common.confirm.ConfirmEmailPhoneFragment
import com.example.ui.views.AddPhoneEmailDialog
import com.example.ui.views.ConfirmPhoneDialog
import com.example.ui.views.ContactsType
import com.example.ui.views.dialogs.MessageDialogWithBrownButton
import com.example.ui.views.toolbar.ToolbarContent
import com.example.ui.views.toolbar.ToolbarIconView
import com.example.util.Utils.maxStateScreen
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider


class MainInfoFragment : BaseFragment<FragmentMainInfoBinding>(), MainInfoContract.View,
    ToolbarFragment {

    @InjectPresenter
    lateinit var presenter: MainInfoPresenter

    @Inject
    lateinit var presenterProvider: Provider<MainInfoPresenter>

    @ProvidePresenter
    fun providePresenter(): MainInfoPresenter = presenterProvider.get().apply {
        type = MainInfoFragmentArgs.fromBundle(requireArguments()).type
        screen = MainInfoFragmentArgs.fromBundle(requireArguments()).screen
    }

    private var onSaveClick: (() -> Unit)? = null
    private lateinit var dialog: AddPhoneEmailDialog

    private val adapter by lazy {
        GroupAdapter<GroupieViewHolder>().apply {
            updateItem(PlaceholderItem(PlaceholderItem.Type.MAIN_INFO_EDIT))
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@MainInfoFragment.adapter
        }
        mBinding.btnSave.setOnClickListener { onSaveClick?.invoke() }
    }


    override fun setPersonalData(user: UserDetail) {
        val item = MainInfoEditItem(
            user.id.toLong(),
            user.gender,
            user.birthday,
            user.address,
            user.personalPhone,
            user.loadUserImage()
        ).apply {
            onEnableNext = { isEnable -> mBinding.btnSave.isEnabled = isEnable }
            onEditPhoneClick = { presenter.onShowPhoneEdit() }
            onImageClick = { presenter.onShowImageEdit() }
            onSaveClick = { presenter.onSaveData(getDataToSave()) }
        }
        adapter.updateItem(item)
    }

    override fun goToNext() {
        when (presenter.type) {
            UserState.MAX -> {
                when (maxStateScreen(presenter.getUserData())) {
                    MaxStateScreenType.BASE ->
                        findNavController().navigate(
                            R.id.maxStatusContactsFragment,
                            bundleOf("screen" to presenter.screen),
                        )
                    MaxStateScreenType.INTERESTS ->
                        findNavController().navigate(
                            R.id.maxStatusInterestsFragment,
                            bundleOf("screen" to presenter.screen)
                        )
                    MaxStateScreenType.EDUCATION ->
                        findNavController().navigate(
                            R.id.maxStatusEducationFragment,
                            bundleOf("screen" to presenter.screen)
                        )
                    MaxStateScreenType.WORK ->
                        findNavController().navigate(
                            R.id.maxStatusWorkFragment,
                            bundleOf("screen" to presenter.screen)
                        )
                    MaxStateScreenType.DONE -> {
                        MessageDialogWithBrownButton(
                            requireContext(),
                            getString(R.string.you_got_max_state),
                            false
                        ).setSelectCallback { baseActions() }
                    }
                }
            }
            UserState.BASE -> baseActionsWithSuccess()
        }
    }

    private fun baseActionsWithSuccess() {
        if (presenter.getEmail()?.value != null && presenter.getEmail()?.isConfirmed != null) {
            MessageDialogWithBrownButton(
                requireContext(),
                getString(R.string.you_got_base_state),
                false
            ).setSelectCallback { baseActions() }
        } else {
            dialog = AddPhoneEmailDialog(requireContext(), ContactsType.EMAIL)
                .setSelectEmailCallback {
                    presenter.checkEmailIsUnique(it)
                    dialog.hideDialog()
                }.setNegativeClickCallback { baseActions() }
        }
    }

    private fun baseActions() {
        when (presenter.screen) {
            1 -> findNavController().popBackStack(R.id.profile_fragment, false)
            2 -> findNavController().popBackStack(R.id.userStateFragment, false)
            3 -> findNavController().popBackStack()
        }
    }


    override fun showEmailNotUnique(email: String) {
        ConfirmPhoneDialog(
            requireContext(), getString(R.string.confirm_email_text, email),
            getString(R.string.revoke), getString(R.string.confirm_phone_positive)
        ).setSelectCallback { if (it) presenter.onShowEmailConfirm(email) }
    }

    override fun showEmailConfirm(email: String) {
        val confirmPhone = ConfirmEmailPhoneFragment(email)
        confirmPhone.show(requireActivity().supportFragmentManager, "main_info_email_dialog")
        confirmPhone.setConfirmCallback {
            baseActions()
        }
    }

    override fun showPhoneEdit() {
        findNavController().navigate(R.id.changePhoneFragment)
    }

    override fun updatePhone(phone: FieldDetails?) {
        adapter.findItemBy<GroupieViewHolder, MainInfoEditItem> { true }?.setPhone(phone)
    }

    override fun showChangeImage() {
        GalleryBottomSheet()
            .setPhotoUpdated { updateImage(it) }
            .show(childFragmentManager)
    }

    override fun updateImage(photo: ImageModel?) {
        adapter.findItemBy<GroupieViewHolder, MainInfoEditItem> { true }?.setImage(photo)
    }

    override fun showCustomLoading() = mBinding.btnSave.showProgressLoading(true)
    override fun hideCustomLoading() = mBinding.btnSave.showProgressLoading(false)

    override fun layout(): Int = R.layout.fragment_main_info
    override val title: CharSequence by lazy { getString(R.string.user_profile_increase_base_state) }
    override fun actionIconContainer(view: ViewGroup) {
        view.apply {
            addView(ToolbarIconView(context).apply {
                setImageAsIcon(R.drawable.ic_close_new)
                setOnClickListener { navigateUp() }
            })
        }
    }

    override fun scrollValue(scroll: Int) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
}
