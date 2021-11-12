package com.example.ui.state.base

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.ImageModel
import com.example.data.models.UserDetail
import com.example.data.models.user.RecommendationFile
import com.example.data.models.user.User
import com.example.extensions.findItemBy
import com.example.extensions.showChangeEmailCompleteDialog
import com.example.extensions.showChangeEmailDialog
import com.example.holders.MainInfoEditItem
import com.example.holders.MaxStateMainInfoEditItem
import com.example.ui.base.BaseFragment
import com.example.ui.state.UserState
import com.example.ui.state.max.MaxStateScreenType
import com.example.ui.userprofile.editfile.UserEditFileFragment
import com.example.ui.views.AddPhoneEmailDialog
import com.example.ui.views.BaseStateDialog
import com.example.ui.views.FinishRegisterDialog
import com.example.ui.views.RegisterDataType
import com.example.ui.views.suggestFieldView.DaDataUtil
import com.example.util.PHONE_PERSONAL
import com.example.util.Utils
import com.example.util.Utils.maxStateScreen
import com.example.util.firstLetterToUppercase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_register_email.*
import kotlinx.android.synthetic.main.fragment_user_edit.*
import javax.inject.Inject
import javax.inject.Provider

class MainInfoFragment: BaseFragment(), MainInfoContract.View {

    private var canUpdateData = true
    private lateinit var dialog: AddPhoneEmailDialog
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
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                hideKeyboard()
                navigateUp()
            }
        })
        ivClose.setOnClickListener { presenter.onClickClose() }
        recyclerView.apply {
            adapter = this@MainInfoFragment.adapter
        }

        btnSave.setOnClickListener { onSaveClick?.invoke() }
    }

    override fun setPersonalData(user: UserDetail) {
        val dataItem = if (canUpdateData) {
            MainInfoEditItem(
                    1,
                    requireContext(),
                    /*user.name,
                    user.lastName,
                    user.middleName?.value,
                    user.middleName?.absent?: true,*/
                    user.gender,
                    user.birthday?.value,
                    DaDataUtil.formatSavedLocation(requireContext(), user.address),
                    user.phone,
                    user.birthday?.isVisible ?: false, user.state?.nameEdited ?: false,
                    user.email, user.image, { isEnable ->
                btnSave.isEnabled = isEnable
            }, { presenter.onConfirmPhoneClick(it) },
                    {
                        AlertDialog.Builder(requireContext())
                                .setTitle(R.string.photo_alert_title)
                                .apply {
                                    if (it) {
                                        setNeutralButton(R.string.photo_alert_remove) { _, _ ->
                                            presenter.onRemovePhotoClick()
                                        }
                                    }
                                }
                                .setPositiveButton(R.string.photo_alert_gallery) { _, _ -> presenter.onTakePhotoFromGalleryClick() }
                                .setNegativeButton(R.string.photo_alert_camera) { _, _ -> presenter.onTakePhotoFromCameraClick() }
                                .show()
                    })
        } else {
            adapter.findItemBy<GroupieViewHolder, MainInfoEditItem> { true }?.setPhoneNumberValid(user.phone?.firstOrNull { it.type == PHONE_PERSONAL }?.isConfirmed?: false)
            canUpdateData = true
            adapter.findItemBy<GroupieViewHolder, MainInfoEditItem> { true }
        }

        adapter.findItemBy<GroupieViewHolder, MainInfoEditItem> { true }?.setPhoneNumberValid(user.phone?.firstOrNull { it.type == PHONE_PERSONAL }?.isConfirmed?: false)

        adapter.update(listOf(dataItem))

        onSaveClick = {
            recyclerView.requestFocus()
            val dataToSave = dataItem?.getDataToSave() as MutableMap
            //if (dataItem.showConfirmEmail()) showChangeEmailComplete(dataItem.getEmail())
            presenter.updateFiles(dataToSave)
        }
    }

    override fun photoUpdated(photo: ImageModel) {
        adapter.findItemBy<GroupieViewHolder, MainInfoEditItem> { true }?.setImage(photo)
    }

    override fun showChangeEmail() = showChangeEmailDialog(presenter::onChangeEmailConfirm)

    override fun showChangeEmailComplete(email: String) {
        dialog.hideDialog()
        showChangeEmailCompleteDialog(email)
        baseActions()
    }

    override fun goToNext() {
        when (presenter.type) {
            UserState.MAX -> {
                when (maxStateScreen(presenter.getUserData())) {
                    MaxStateScreenType.BASE ->
                        findNavController().navigate(MainInfoFragmentDirections.actionMainInfoFragmentToMaxStateMainInfoFragment().setScreen(presenter.screen))
                    MaxStateScreenType.INTERESTS ->
                        findNavController().navigate(MainInfoFragmentDirections.actionMainInfoFragmentToBaseStateInterestsFragment().setScreen(presenter.screen))
                    MaxStateScreenType.WORK ->
                        findNavController().navigate(MainInfoFragmentDirections.actionMainInfoFragmentToMaxStateWorkFragment().setScreen(presenter.screen))
                    MaxStateScreenType.EDUCATION ->
                        findNavController().navigate(MainInfoFragmentDirections.actionMainInfoFragmentToMaxStateEducationFragment().setScreen(presenter.screen))
                    MaxStateScreenType.DONE -> {
                        BaseStateDialog(resources.getString(R.string.you_got_max_state), requireActivity())
                                .setSelectCallback {
                                    when (presenter.screen) {
                                        1 -> findNavController().popBackStack(R.id.profile_fragment, false)
                                        2 -> findNavController().popBackStack(R.id.userStateFragment, false)
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
                                presenter.sendEmail(it.value)
                            }.setNegativeClickCallback { baseActions() }
                }
            }
        }
    }

    private fun baseActionsWithSuccess() {
        BaseStateDialog(resources.getString(R.string.you_got_base_state), requireActivity())
                .setSelectCallback {
                    baseActions()
        }
    }

    private fun baseActions() {
        /*BaseStateDialog(resources.getString(R.string.you_got_base_state), requireActivity())
                .setSelectCallback {*/
                    when (presenter.screen) {
                        1 -> findNavController().popBackStack(R.id.profile_fragment, false)
                        2 -> findNavController().popBackStack(R.id.userStateFragment, false)
                        3 -> findNavController().popBackStack()
                    }
                //}
    }

    override fun showPhoneConfirm(phone: String) {
        canUpdateData = false
        findNavController().navigate(MainInfoFragmentDirections.actionMainInfoFragmentToPasswordConfirmFragment(Utils.validatePhoneBeforeSend(phone.replace(" ", "").replace("-", ""))/*phone.replace(" ", "").replace("-", "")*/))
    }

    override fun showUpdateError(message: String?) {
        val title = getString(R.string.profile_edit_request_error)
        Toast.makeText(requireContext(), message?.let { "$title: $it" }
                ?: title, Toast.LENGTH_SHORT).show()
    }
}