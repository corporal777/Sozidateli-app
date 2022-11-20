package com.example.ui.state.base

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.FieldDetails
import com.example.data.models.ImageModel
import com.example.data.models.UserDetail
import com.example.databinding.FragmentMainInfoBinding
import com.example.extensions.findItemBy
import com.example.extensions.showChangeEmailCompleteDialog
import com.example.holders.MainInfoEditItem
import com.example.ui.base.BaseFragmentNew
import com.example.ui.main.MainActivity
import com.example.ui.state.UserState
import com.example.ui.state.max.MaxStateScreenType
import com.example.ui.userprofile.read.settings.confirm_phone_email.ConfirmEmailPhoneFragment
import com.example.ui.views.AddPhoneEmailDialog
import com.example.ui.views.ConfirmPhoneDialog
import com.example.ui.views.RegisterDataType
import com.example.ui.views.SetPasswordDialog
import com.example.ui.views.dialogs_new.MessageDialogWithBrownButton
import com.example.ui.views.suggestFieldView.DaDataUtil
import com.example.ui.views.toolbar.SimpleTitleToolbar
import com.example.util.PHONE_PERSONAL
import com.example.util.Utils
import com.example.util.Utils.maxStateScreen
import com.example.util.phoneToServer
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import onScrolled
import javax.inject.Inject
import javax.inject.Provider


class MainInfoFragment : BaseFragmentNew<FragmentMainInfoBinding>(), MainInfoContract.View,
    SimpleTitleToolbar {

    private lateinit var passwordDialog: SetPasswordDialog
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
    private var onConfirmClick: ((phone: String?) -> Unit)? = null
    private var onImageClick: ((canRemove: Boolean) -> Unit)? = null

    private val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarTitle()
        mBinding.recyclerView.apply {
            adapter = this@MainInfoFragment.adapter
            onScrolled { _, _ ->
                presenter.changeAppBarElevation(this.computeVerticalScrollOffset())
            }
        }

        mBinding.btnSave.setOnClickListener { onSaveClick?.invoke() }
    }

    override fun setPersonalData(user: UserDetail) {
        val phone = user.phone?.firstOrNull { it.type == PHONE_PERSONAL }
        adapter.update(listOf(
            MainInfoEditItem(
                1,
                requireActivity(),
                user.gender,
                user.birthday?.value,
                DaDataUtil.formatSavedLocation(requireContext(), user.address),
                user.phone,
                user.birthday?.isVisible ?: false,
                user.state?.nameEdited ?: false,
                user.image,
                isEnableNext = { isEnable ->
                    mBinding.btnSave.isEnabled = isEnable
                }, confirmPhoneClick = {
                    onConfirmClick?.invoke(it)
                },
                onImageClick = {
                    showChangePhoto(it)
                }).apply {
                dataItem = this
            }
        ))

        onConfirmClick = {
            presenter.setCanGoNext(false)
            if (phone?.isConfirmed == true) {
                showCheckPassword(dataItem.getValidatedPhone())
            } else {
                presenter.onConfirmPhoneClick(dataItem.getValidatedPhone())
            }
        }
        onSaveClick = {
            presenter.setCanGoNext(true)
            if (dataItem.checkDataValid()) {
                if (user.phone?.firstOrNull()?.isConfirmed == true) {
                    if (dataItem.newPhoneIsConfirmed()) {
                        presenter.updateFiles(dataItem.getDataToSave())
                    } else {
                        showCheckPassword(dataItem.getValidatedPhone())
                    }
                } else {
                    presenter.updateFiles(dataItem.getDataToSave())
                }
            }

        }
    }

    override fun photoUpdated(photo: ImageModel) {
        adapter.findItemBy<GroupieViewHolder, MainInfoEditItem> { true }?.setImage(photo)
    }

    override fun goToNext() {
        when (presenter.type) {
            UserState.MAX -> {
                when (maxStateScreen(presenter.getUserData())) {
                    MaxStateScreenType.BASE ->
                        findNavController().navigate(
                            MainInfoFragmentDirections.actionMainInfoFragmentToMaxStateMainInfoFragment()
                                .setScreen(presenter.screen)
                        )
                    MaxStateScreenType.INTERESTS ->
                        findNavController().navigate(
                            MainInfoFragmentDirections.actionMainInfoFragmentToBaseStateInterestsFragment()
                                .setScreen(presenter.screen)
                        )
                    MaxStateScreenType.WORK ->
                        findNavController().navigate(
                            MainInfoFragmentDirections.actionMainInfoFragmentToMaxStateWorkFragment()
                                .setScreen(presenter.screen)
                        )
                    MaxStateScreenType.EDUCATION ->
                        findNavController().navigate(
                            MainInfoFragmentDirections.actionMainInfoFragmentToMaxStateEducationFragment()
                                .setScreen(presenter.screen)
                        )
                    MaxStateScreenType.DONE -> {
                        MessageDialogWithBrownButton(
                            requireContext(),
                            getString(R.string.you_got_max_state)
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
                    dialog = AddPhoneEmailDialog(requireActivity(), RegisterDataType.EMAIL)
                        .setSelectCallback {
                            presenter.checkEmailIsUnique(it.value)
                        }.setNegativeClickCallback { baseActions() }
                }
            }
        }
    }

    private fun baseActionsWithSuccess() {
        MessageDialogWithBrownButton(
            requireContext(),
            getString(R.string.you_got_base_state)
        ).setSelectCallback {
            baseActions()
        }
    }

    private fun baseActions() {
        when (presenter.screen) {
            1 -> findNavController().popBackStack(R.id.profile_fragment, false)
            2 -> findNavController().popBackStack(R.id.userStateFragment, false)
            3 -> findNavController().popBackStack()
        }
    }

    override fun showPhoneNotUnique(phone: String) {
        ConfirmPhoneDialog(
            requireContext(),
            getString(R.string.confirm_phone_text, phone),
            getString(R.string.revoke),
            getString(R.string.confirm_phone_positive)
        ).setSelectCallback {
            if (it) {
                showPhoneConfirm(phone)
            }
        }
    }

    override fun showEmailNotUnique(email: String) {
        ConfirmPhoneDialog(
            requireContext(), getString(R.string.confirm_email_text, email),
            getString(R.string.revoke), getString(R.string.confirm_phone_positive)
        )
            .setSelectCallback {
                if (it) {
                    showEmailConfirm(email)
                }
            }
    }


    override fun showCheckPassword(phone: String?) {
        passwordDialog = SetPasswordDialog(requireActivity())
            .setSelectCallback {
                presenter.checkPassword(it, phone ?: "")
            }
    }

    override fun hideCheckPassword() {
        passwordDialog.hideDialog()
    }

    override fun showPhoneConfirm(phone: String) {
        val confirmPhone = ConfirmEmailPhoneFragment(phone)
        confirmPhone.show(requireActivity().supportFragmentManager, "main_info_phone_dialog")
        confirmPhone.setConfirmCallback {
            updatePhoneConfirmation(phone)
        }
    }

    override fun showEmailConfirm(email: String) {
        dialog.hideDialog()
        val confirmPhone = ConfirmEmailPhoneFragment(email)
        confirmPhone.show(requireActivity().supportFragmentManager, "main_info_email_dialog")
        confirmPhone.setConfirmCallback {
            presenter.updateEmail(email)
        }
    }

    override fun updatePhoneConfirmation(phone: String) {
        adapter.findItemBy<GroupieViewHolder, MainInfoEditItem> { true }
            ?.updatePhoneConfirmation(true)
        if (presenter.isCanGoNext()) {
            presenter.updateFiles(dataItem.getDataToSave())
        }
    }

    override fun showChangeEmailComplete(email: String) {
        baseActions()
    }


    private fun setToolbarTitle() {
        val actionIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_close_new)
        setToolbarTitleAndIcon(
            getString(R.string.user_profile_main_info),
            actionIcon,
            action = {
                presenter.onClickClose()
            })
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
}