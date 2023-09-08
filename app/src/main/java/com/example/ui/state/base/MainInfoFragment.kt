package com.example.ui.state.base

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
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
import com.example.ui.state.UserState
import com.example.ui.state.maxNew.MaxStateScreenType
import com.example.ui.state.maxNew.education.MaxStatusEducationFragmentArgs
import com.example.ui.state.maxNew.interests.MaxStatusInterestsFragmentArgs
import com.example.ui.state.maxNew.mainInfo.MaxStatusContactsFragmentArgs
import com.example.ui.state.maxNew.work.MaxStatusWorkFragmentArgs
import com.example.ui.userprofile.read.settings.change_phone.ChangePhoneFragment
import com.example.ui.userprofile.read.settings.confirm_phone_email.ConfirmEmailPhoneFragment
import com.example.ui.views.AddPhoneEmailDialog
import com.example.ui.views.ConfirmPhoneDialog
import com.example.ui.views.RegisterDataType
import com.example.ui.views.SetPasswordDialog
import com.example.ui.views.dialogs_new.MessageDialogWithBrownButton
import com.example.ui.views.suggestFieldView.address.DaDataUtil
import com.example.ui.views.toolbar.ToolbarContent
import com.example.ui.views.toolbar.ToolbarIconView
import com.example.util.Utils.maxStateScreen
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import onScrolled
import javax.inject.Inject
import javax.inject.Provider


class MainInfoFragment : BaseFragment<FragmentMainInfoBinding>(), MainInfoContract.View,
    ToolbarFragment {

    private lateinit var dialog: AddPhoneEmailDialog

    private lateinit var dataItem: MainInfoEditItem

    override fun layout(): Int = R.layout.fragment_main_info

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

    private val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.recyclerView.apply {
            adapter = this@MainInfoFragment.adapter
        }

        mBinding.btnSave.setOnClickListener { onSaveClick?.invoke() }
    }

    override fun setPlaceholder() {
        adapter.updateItem(PlaceholderItem(PlaceholderItem.Type.MAIN_INFO_EDIT))
    }

    override fun setPersonalData(user: UserDetail) {
        adapter.updateItem(
            MainInfoEditItem(
                1,
                user.gender,
                user.birthday?.value,
                DaDataUtil.formatSavedLocation(requireContext(), user.address),
                user.phone,
                user.birthday?.isVisible,
                user.loadUserImage(),
                isEnableNext = { isEnable -> mBinding.btnSave.isEnabled = isEnable },
                onEditPhoneClick = { presenter.onShowPhoneEdit(it) },
                onImageClick = { showChangePhoto(it) }
            ).apply { dataItem = this }
        )

        onSaveClick = {
            if (dataItem.checkDataValid()) presenter.onSaveData(dataItem.getDataToSave())
        }
    }

    override fun photoUpdated(photo: ImageModel?) {
        adapter.findItemBy<GroupieViewHolder, MainInfoEditItem> { true }?.setImage(photo)
    }

    override fun goToNext() {
        when (presenter.type) {
            UserState.MAX -> {
                when (maxStateScreen(presenter.getUserData())) {
                    MaxStateScreenType.BASE ->
                        findNavController().navigate(
                            R.id.maxStatusContactsFragment,
                            MaxStatusContactsFragmentArgs.Builder().setScreen(presenter.screen)
                                .build().toBundle()
                        )
                    MaxStateScreenType.INTERESTS ->
                        findNavController().navigate(
                            R.id.maxStatusInterestsFragment,
                            MaxStatusInterestsFragmentArgs.Builder().setScreen(presenter.screen)
                                .build().toBundle()
                        )
                    MaxStateScreenType.EDUCATION ->
                        findNavController().navigate(
                            R.id.maxStatusEducationFragment,
                            MaxStatusEducationFragmentArgs.Builder().setScreen(presenter.screen)
                                .build().toBundle()
                        )
                    MaxStateScreenType.WORK ->
                        findNavController().navigate(
                            R.id.maxStatusWorkFragment,
                            MaxStatusWorkFragmentArgs.Builder().setScreen(presenter.screen).build()
                                .toBundle()
                        )
                    MaxStateScreenType.DONE -> {
                        MessageDialogWithBrownButton(
                            requireContext(),
                            getString(R.string.you_got_max_state),
                            false
                        )
                            .setSelectCallback {
                                when (presenter.screen) {
                                    1 -> findNavController().popBackStack(
                                        R.id.profile_fragment,
                                        false
                                    )
                                    2 -> findNavController().popBackStack(
                                        R.id.userStateFragment,
                                        false
                                    )
                                }
                            }
                    }
                }
            }
            UserState.BASE -> {
                if (presenter.getEmail()?.value != null && presenter.getEmail()?.isConfirmed != null) {
                    baseActionsWithSuccess()
                } else {
                    dialog = AddPhoneEmailDialog(requireContext(), RegisterDataType.EMAIL)
                        .setSelectCallback {
                            presenter.checkEmailIsUnique(it.value)
                            dialog.hideDialog()
                        }.setNegativeClickCallback { baseActions() }
                }
            }
        }
    }

    private fun baseActionsWithSuccess() {
        MessageDialogWithBrownButton(
            requireContext(),
            getString(R.string.you_got_base_state),
            false
        ).setSelectCallback { baseActions() }
    }

    private fun baseActions() {
        when (presenter.screen) {
            1 -> findNavController().popBackStack(R.id.profile_fragment, false)
            2 -> findNavController().popBackStack(R.id.userStateFragment, false)
            3 -> findNavController().popBackStack()
        }
    }

    override fun showPhoneEdit(phone: String?) {
        ChangePhoneFragment(presenter.getPhone())
            .setPhoneChangedCallback {
                adapter.findItemBy<GroupieViewHolder, MainInfoEditItem> { true }?.setPhone(it)
            }.show(requireActivity().supportFragmentManager)
    }


    override fun showEmailNotUnique(email: String) {
        ConfirmPhoneDialog(
            requireContext(), getString(R.string.confirm_email_text, email),
            getString(R.string.revoke), getString(R.string.confirm_phone_positive)
        )
            .setSelectCallback {
                if (it) presenter.onShowEmailConfirm(email)
            }
    }

    override fun showEmailConfirm(email: String) {
        val confirmPhone = ConfirmEmailPhoneFragment(email)
        confirmPhone.show(requireActivity().supportFragmentManager, "main_info_email_dialog")
        confirmPhone.setConfirmCallback {
            baseActions()
        }
    }

    private fun showChangePhoto(change: Boolean) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.photo_alert_title)
            .apply {
                if (change) {
                    setNeutralButton(R.string.photo_alert_remove) { _, _ ->
                        presenter.onRemovePhotoClick()
                    }
                }
            }
            .setPositiveButton(R.string.photo_alert_gallery) { _, _ -> presenter.onTakePhotoFromGalleryClick() }
            .setNegativeButton(R.string.photo_alert_camera) { _, _ -> presenter.onTakePhotoFromCameraClick() }
            .show()
    }

    override val title: CharSequence by lazy { getString(R.string.user_profile_increase_base_state) }
    override fun actionIconContainer(view: ViewGroup) {
        view.apply {
            addView(ToolbarIconView(context).apply {
                setImageAsIcon(R.drawable.ic_close_new)
                setOnClickListener { presenter.onClickClose() }
            })
        }
    }

    override fun scrollValue(scroll: (value: Int) -> Unit) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
}
