package com.example.ui.userprofile.edit

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.*
import com.example.databinding.FragmentUserEditBinding
import com.example.extensions.*
import com.example.holders.*
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.userprofile.read.settings.confirm_phone_email.ConfirmEmailPhoneFragment
import com.example.ui.views.ConfirmPhoneDialog
import com.example.ui.views.InfoDialog
import com.example.ui.views.SetPasswordDialog
import com.example.ui.views.dialogs_new.TitleMessageDialog
import com.example.ui.views.suggestFieldView.address.DaDataUtil
import com.example.ui.views.toolbar.ToolbarContent
import com.example.util.FileUtils
import com.example.util.PHONE_PERSONAL
import com.example.util.PHONE_WORK
import com.example.util.UriUtils
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import onBackPressedCallback
import javax.inject.Inject
import javax.inject.Provider

class UserEditFragment : BaseFragment<FragmentUserEditBinding>(), UserEditContract.View,
    ToolbarFragment {

    private lateinit var passwordDialog: SetPasswordDialog

    private val mimeTypes = arrayOf("image/*", "application/pdf")

    private lateinit var data: ProfileContactsEditItem
    private lateinit var toolbarContent: ToolbarContent

    override fun layout() = R.layout.fragment_user_edit

    @InjectPresenter
    lateinit var presenter: UserEditPresenter

    @Inject
    lateinit var presenterProvider: Provider<UserEditPresenter>

    @ProvidePresenter
    fun providePresenter(): UserEditPresenter = presenterProvider.get().apply {
        editType = UserEditFragmentArgs.fromBundle(requireArguments()).type
    }

    private val galleryImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { uri ->
            uri?.let {
                it.data?.data?.let { file ->
                    val filePath = FileUtils.getPath(requireContext(), file)
                    val mimeType = FileUtils.getMimeType(requireContext(), file)

                    if (filePath.isEmpty()) {
                        val path = UriUtils.pickedExistingPicture(requireContext(), file).path
                        val type = UriUtils.getMimeType(requireContext(), file) ?: ""
                        presenter.onFilePicked(path, type)
                    } else presenter.onFilePicked(filePath, mimeType)
                }
            }
        }

    private val adapter = GroupAdapter<GroupieViewHolder>()

    private val onItemExpandChange: OnExpandChange<*> = {
        if (it.isExpanded) {
            val position = adapter.getAdapterPosition(it.titleItem)
            (mBinding.recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
                position,
                0
            )
        }
    }

    private var onSaveClick: (() -> Unit)? = null
    private var onConfirmClick: ((phone: String) -> Unit)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressedCallback(true) {
            hideKeyboard()
            presenter.onNavigateUpRequest()
        }
        mBinding.recyclerView.apply {
            adapter = this@UserEditFragment.adapter
        }
        mBinding.btnSave.setOnClickListener { onSaveClick?.invoke() }
    }


    override fun setPlaceholder(type: UserEditDataType) {
        when (type) {
            UserEditDataType.PERSONAL -> adapter.updateItem(PlaceholderItem(PlaceholderItem.Type.CONTACTS))
            UserEditDataType.CONTACTS -> adapter.updateItem(PlaceholderItem(PlaceholderItem.Type.CONTACTS))
            UserEditDataType.INTERESTS -> adapter.update(List(7) { PlaceholderItem(PlaceholderItem.Type.INTERESTS) })
        }
    }


    override fun setPersonalData(user: UserDetail, state: String) {
        val dataItem = ProfileDataPersonalEditItem(
            user.id.toLong(),
            requireContext(),
            user.gender,
            user.birthday,
            DaDataUtil.formatSavedLocation(requireContext(), user.address),
            user.notes,
        )

        val files = ProfileDataFileEditableGroup(
            user.binds?.recommendationFile ?: emptyList(),
            user.filesCount,
            { presenter.onAddFileClick() },
            { presenter.onFileClick(it) },
            { presenter.onDeleteFilesClick(it) }
        )

        adapter.update(listOf(dataItem, files))

        onSaveClick = {
            mBinding.recyclerView.requestFocus()
            showEditWarning(
                presenter.getBaseUserState(),
                presenter.getMaxUserState(),
                dataItem.checkBaseFieldsValid(),
                dataItem.checkMaxFieldsValid()
            ) {
                val dataToSave = dataItem.getDataToSave() as MutableMap
                val file = files.getCurrentFilesToSave()
                presenter.onSavePersonalDataClick(file.toMutableList(), dataToSave)
            }
        }
    }

    override fun setInterestsData(interests: Map<InterestNew, List<UserInterest>>) {
        val findUserInterests: () -> List<InterestNew> = {
            interests.values.flatten().filter { item -> item.isUserInterest }
                .map { item -> item.interest }
        }

        var userInterests = findUserInterests()

        if (userInterests.isNullOrEmpty()) mBinding.btnSave.isEnabled = false

        adapter.update(interests.map {
            val parent = it.key
            val childList = it.value
            ProfileExpandableSubtitleGroup(
                parent.name ?: "",
                onExpandChange = onItemExpandChange
            ).apply {
                titleItem.badgeCount = childList.count { child -> child.isUserInterest }
                val interestsItems = childList.mapIndexed { index, interest ->
                    ProfileDataInterestEditItem(interest, index != childList.size - 1) {
                        userInterests = findUserInterests()

                        val count = childList.count { child -> child.isUserInterest }
                        titleItem.apply {
                            badgeCount = count
                            notifyChanged(count)
                        }
                        if (count > 0) mBinding.btnSave.isEnabled = true
                    }
                }

                addAll(interestsItems)
            }
        })

        onSaveClick = {
            showEditWarning(
                presenter.getBaseUserState(),
                presenter.getMaxUserState(), false, userInterests.isEmpty()
            ) {
                presenter.onSaveInterestsClick(userInterests)
            }
        }

    }

    override fun setContactsData(user: UserDetail) {
        val userPhone = user.phone?.firstOrNull { it.type == PHONE_PERSONAL }
        val workPhone = user.phone?.firstOrNull { it.type == PHONE_WORK }

        val item = ProfileContactsEditItem(
            requireContext(),
            userPhone,
            workPhone,
            user.contactInformation.socialLinks,
            user.contactInformation.site,
            user.email,
            user.email?.isVisible ?: false,
            user.contactInformation.emails ?: emptyList(),
            presenter::onChangeEmailClick
        ) {
            onConfirmClick?.invoke(it)
        }.apply { data = this }
        adapter.updateItem(item)

        onSaveClick = {
            presenter.canUpdate(true)
            if (item.checkDataValid()) {
                if (userPhone?.isConfirmed == true) {
                    if (item.newPhoneIsConfirmed()) {
                        showEditWarning(
                            presenter.getBaseUserState(),
                            presenter.getMaxUserState(),
                            item.checkBaseFieldsValid(),
                            item.checkMaxFieldsValid()
                        ) {
                            presenter.onSaveContactsClick(item.getDataToSave())
                        }
                    } else showEnterPassword(item.getValidatedPhone())
                } else presenter.onSaveContactsClick(item.getDataToSave())
            }
        }
        onConfirmClick = {
            presenter.canUpdate(false)
            if (item.isNewPhoneIsValid()) {
                presenter.checkPhoneIsUnique(item.getValidatedPhone())
            }
        }
    }

    override fun showChangeEmail() {
        TitleMessageDialog(
            requireContext(),
            "",
            message = getString(R.string.change_email_text),
            btnPositiveText = getString(R.string.change_email_positive_button),
            btnNegativeText = getString(R.string.revoke)
        ).setPositiveSelectCallback {
            findNavController().navigate(R.id.user_profile_settings_fragment)
        }
    }

    override fun showEnterPassword(phone: String) {
        passwordDialog = SetPasswordDialog(requireActivity())
            .setSelectCallback {
                presenter.checkPassword(it, phone)
            }
    }

    override fun hideEnterPassword() {
        passwordDialog.hideDialog()
    }

    override fun showPhoneConfirm(phone: String) {
        val confirmEmailPhoneDialog = ConfirmEmailPhoneFragment(phone)
        confirmEmailPhoneDialog.show(
            requireActivity().supportFragmentManager,
            "confirm_phone_dialog"
        )
        confirmEmailPhoneDialog.setConfirmCallback {
            updatePhoneConfirmation(phone)
        }
    }

    override fun updatePhoneConfirmation(phone: String) {
        val item = adapter.findItemBy<GroupieViewHolder, ProfileContactsEditItem> { true }
        item?.setPhoneConfirmed(true)
        if (presenter.isWithUpdate()) {
            presenter.onSaveContactsClick(data.getDataToSave())
        }
    }

    override fun showPhoneNotUnique(phone: String) {
        ConfirmPhoneDialog(
            requireContext(), getString(R.string.confirm_phone_text, phone),
            getString(R.string.revoke), getString(R.string.confirm_phone_positive)
        )
            .setSelectCallback {
                if (it) {
                    presenter.onShowPhoneConfirm(phone)
                }
            }
    }


    override fun showUpdateError(message: String?) {
        val title = getString(R.string.profile_edit_request_error)
        Toast.makeText(requireContext(), message?.let { "$title: $it" }
            ?: title, Toast.LENGTH_SHORT).show()
    }

    override fun showFileSelector() {
        val intent = Intent()
        intent.type = "*/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        galleryImage.launch(intent)
    }

    override fun downloadFile(file: String) {
        val uri = Uri.parse(file)
        try {
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (e: ActivityNotFoundException) {
            showRequestErrorMessage()
        }
    }

    override fun addUserFile(file: FileModel, fileCount: Int) {
        adapter.findGroup<ProfileDataFileEditableGroup> { true }?.addFileItem(file, fileCount)
    }

    override fun deleteUserFile(file: FileModel, fileCount: Int) {
        adapter.findGroup<ProfileDataFileEditableGroup> { true }?.removeFileItem(file, fileCount)
    }

    override fun hideDeleteUserFile(file: FileModel) {
        adapter.findGroup<ProfileDataFileEditableGroup> { true }?.hideFileDeleteLoading(file)
    }

    override fun showFileUploadLoading() {
        adapter.findGroup<ProfileDataFileEditableGroup> { true }?.showUploadLoading()
    }

    override fun hideFileUploadLoading() {
        adapter.findGroup<ProfileDataFileEditableGroup> { true }?.hideUploadLoading()
    }

    override fun showCustomLoading() {
        mBinding.apply { btnSave.showProgressLoading(true) }
    }

    override fun hideCustomLoading(){
        mBinding.apply { btnSave.showProgressLoading(false) }
    }

    override fun setPersonalTitle() = setTitle(getString(R.string.user_profile_main_info))
    override fun setContactsTitle() = setTitle(getString(R.string.user_profile_contacts))
    override fun setPhoneTitle() = setTitle(getString(R.string.profile_phone_mobile))
    override fun setEducationTitle() = setTitle(getString(R.string.profile_title_education))
    override fun setWorkTitle() = setTitle(getString(R.string.profile_work_experience))
    override fun setInterestsTitle() = setTitle(getString(R.string.profile_interests))


    private fun setTitle(title: String) = toolbarContent.setToolbarTitle(title)
    override fun navigateUp() = presenter.onNavigateUpRequest()
    override fun navigateUpChecked() = super.navigateUp()
    override fun saveOnClick(saveOnClick: Boolean) {
        mBinding.btnSave.isVisible = saveOnClick
    }
    override val title: CharSequence = ""
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: (value: Int) -> Unit) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {
        this.toolbarContent = toolbarContent
    }
}