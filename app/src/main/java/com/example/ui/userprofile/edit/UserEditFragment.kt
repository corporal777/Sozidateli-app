package com.example.ui.userprofile.edit

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.*
import com.example.databinding.FragmentUserEditBinding
import com.example.extensions.findGroupBy
import com.example.extensions.findItemBy
import com.example.holders.*
import com.example.interfaces.ToolbarFragmentNew
import com.example.ui.base.BaseFragmentNew
import com.example.ui.userprofile.academicdegree.EditDegreeFragment.Companion.DEGREES_LEVEL
import com.example.ui.userprofile.academicdegree.EditDegreeFragment.Companion.DEGREE_EDIT_CODE
import com.example.ui.userprofile.academicdegree.EditDegreeFragment.Companion.ITEM_POSITION
import com.example.ui.userprofile.academicdegree.EditDegreeFragment.Companion.SCIENCES_LEVEL
import com.example.ui.userprofile.editfile.UserEditFileFragment.Companion.FILE_EDIT_CODE
import com.example.ui.userprofile.read.settings.confirm_phone_email.ConfirmEmailPhoneFragment
import com.example.ui.views.ConfirmPhoneDialog
import com.example.ui.views.InfoDialog
import com.example.ui.views.SetPasswordDialog
import com.example.ui.views.dialogs_new.TitleMessageDialog
import com.example.ui.views.suggestFieldView.address.DaDataUtil
import com.example.ui.views.toolbar.ToolbarContent
import com.example.util.*
import com.vincent.filepicker.Constant
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import onScrolled
import javax.inject.Inject
import javax.inject.Provider

class UserEditFragment : BaseFragmentNew<FragmentUserEditBinding>(), UserEditContract.View,
    ToolbarFragmentNew {

    private lateinit var passwordDialog: SetPasswordDialog

    var mimeTypes = arrayOf("image/*", "application/pdf")

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
                    } else {
                        presenter.onFilePicked(filePath, mimeType)
                    }
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentFragmentManager.setFragmentResultListener(
            FILE_EDIT_CODE,
            this
        ) { requestKey, result ->
        }
        parentFragmentManager.setFragmentResultListener(
            DEGREE_EDIT_CODE,
            this
        ) { requestKey, result ->
            val degreesLevel = result.getString(DEGREES_LEVEL)
            val sciencesLevel = result.getString(SCIENCES_LEVEL)
            val position = result.getInt(ITEM_POSITION)
            (adapter.getGroup(1) as ProfileDataEducationEditGroup).addDegree(
                degreesLevel,
                sciencesLevel,
                position,
                false
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    hideKeyboard()
                    presenter.onNavigateUpRequest()
                }
            })

        mBinding.recyclerView.apply {
            adapter = this@UserEditFragment.adapter
            onScrolled { _, _ ->
                presenter.changeAppBarElevation(this.computeVerticalScrollOffset())
            }
        }

        mBinding.btnSave.setOnClickListener { onSaveClick?.invoke() }
    }



    override fun setPersonalData(user: UserDetail, state: String) {
        val dataItem = ProfileDataPersonalEditNewItem(
            1,
            requireContext(),
            user.gender,
            user.birthday?.value,
            user.birthday?.isVisible ?: false,
            DaDataUtil.formatSavedLocation(requireContext(), user.address),
            user.notes,
        ) { showWhyUserShouldAddDataToNotesField() }

        val files = ProfileDataAdditionalFilesEditNewGroup(
            2,
            requireContext(),
            user.binds?.recommendationFile ?: emptyList(),
            { presenter.onAddFileClick() },
            { presenter.onFileClick(it) },
            { presenter.onDeleteFilesClick(it) }
        )

        adapter.update(listOf(dataItem, files))

        onSaveClick = {
            mBinding.recyclerView.requestFocus()
            if (dataItem.checkDataValid()) {
                showEditWarning(
                    presenter.getBaseUserState(),
                    presenter.getMaxUserState(),
                    dataItem.checkBaseFieldsValid(),
                    dataItem.checkMaxFieldsValid()
                ) {
                    val dataToSave = dataItem.getDataToSave() as MutableMap
                    val file = files.getCurrentFilesToSave()
                    presenter.updateFiles(file.toMutableList(), dataToSave)
                }
            }
        }
    }

    override fun setInterestsData(interests: Map<InterestNew, List<UserInterest>>) {
        val findUserInterests: () -> List<InterestNew> = {
            interests.values.flatten().filter { item -> item.isUserInterest }
                .map { item -> item.interest }
        }

        var userInterests = findUserInterests()

        if (userInterests.isNullOrEmpty()) {
            mBinding.btnSave.isEnabled = false
        }

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
                        if (count > 0) {
                            mBinding.btnSave.isEnabled = true
                        }
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
                if (userInterests.isNullOrEmpty()) {
                    mBinding.btnSave.isEnabled = false
                }
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
        }.apply {
            data = this
        }
        adapter.update(listOf(item))

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
                    } else {
                        showEnterPassword(item.getValidatedPhone())
                    }
                } else {
                    presenter.onSaveContactsClick(item.getDataToSave())
                }
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

    private fun showWhyUserShouldAddDataToNotesField() {
        InfoDialog(
            requireContext(),
            getString(R.string.profile_edit_additional_notes_data),
            requireActivity()
        )
            .setSelectCallback { }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, result: Intent?) {
        super.onActivityResult(requestCode, resultCode, result)

        if (resultCode == RESULT_OK) {
            if (requestCode == Constant.REQUEST_CODE_PICK_FILE) {
                result?.data?.let {
                    val file = FileUtils.getPath(context, it)
                    val mimeType = FileUtils.getMimeType(context, it)
                    presenter.onFilePicked(file, mimeType)
                }
            }
        }
    }

    override fun addNewUserFile(file: FileModel) {
        adapter.findGroupBy<GroupieViewHolder, ProfileDataAdditionalFilesEditNewGroup> {
            true
        }?.addNewFile(file)
    }

    override fun deleteUserFile(file: FileModel) {
        adapter.findGroupBy<GroupieViewHolder, ProfileDataAdditionalFilesEditNewGroup> {
            true
        }?.deleteUserFile(file)
    }


    //override fun setMainTitle() = setTitle(getString(R.string.profile_edit_name_and_photo))
    override fun setPersonalTitle() = setTitle(getString(R.string.user_profile_main_info))
    override fun setContactsTitle() = setTitle(getString(R.string.user_profile_contacts))
    override fun setPhoneTitle() = setTitle(getString(R.string.profile_phone_mobile))
    override fun setEducationTitle() = setTitle(getString(R.string.profile_title_education))
    override fun setWorkTitle() = setTitle(getString(R.string.profile_work_experience))
    override fun setInterestsTitle() = setTitle(getString(R.string.profile_interests))
    //override fun setAdditionalNotesTitle() = setTitle(getString(R.string.profile_notes))
    //override fun setAdditionalFilesTitle() = setTitle(getString(R.string.profile_files_title))

    private fun setTitle(title: String) {
        toolbarContent.setToolbarTitle(title)
    }

    override fun navigateUp() {
        presenter.onNavigateUpRequest()
    }

    override fun navigateUpChecked() {
        super.navigateUp()
    }

    override fun saveOnClick(saveOnClick: Boolean) {
        mBinding.btnSave.isVisible = saveOnClick
    }

    override val title: CharSequence = ""
    override val actionIconHidden: Boolean = true
    override val actionIcon: Drawable? = null
    override fun actionIconClick() {}
    override fun toolbarTitleClick() {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {
        this.toolbarContent = toolbarContent
    }
}