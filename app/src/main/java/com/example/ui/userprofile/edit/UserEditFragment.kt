package com.example.ui.userprofile.edit

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.text.util.Linkify
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.text.toSpannable
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.*
import com.example.data.models.user.RecommendationFile
import com.example.data.models.user.User
import com.example.databinding.FragmentUserEditBinding
import com.example.extensions.findGroupBy
import com.example.extensions.findItemBy
import com.example.holders.*
import com.example.ui.base.BaseFragmentNew
import com.example.ui.main.MainActivity
import com.example.ui.userprofile.academicdegree.EditDegreeFragment.Companion.DEGREES_LEVEL
import com.example.ui.userprofile.academicdegree.EditDegreeFragment.Companion.DEGREE_EDIT_CODE
import com.example.ui.userprofile.academicdegree.EditDegreeFragment.Companion.ITEM_POSITION
import com.example.ui.userprofile.academicdegree.EditDegreeFragment.Companion.SCIENCES_LEVEL
import com.example.ui.userprofile.editfile.UserEditFileFragment.Companion.FILE_EDIT_CODE
import com.example.ui.userprofile.editfile.UserEditFileFragment.Companion.FILE_PATH
import com.example.ui.userprofile.read.settings.confirm_phone_email.ConfirmEmailPhoneFragment
import com.example.ui.views.*
import com.example.ui.views.dialogs_new.TitleMessageDialog
import com.example.ui.views.suggestFieldView.DaDataUtil
import com.example.ui.views.toolbar.SimpleTitleToolbar
import com.example.util.*
import com.vincent.filepicker.Constant
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import onScrolled
import javax.inject.Inject
import javax.inject.Provider

class UserEditFragment : BaseFragmentNew<FragmentUserEditBinding>(), UserEditContract.View, SimpleTitleToolbar {

    private lateinit var passwordDialog: SetPasswordDialog

    var mimeTypes = arrayOf("image/*", "application/pdf")
    private var isUpdateInfo = true
    private var mainInfoFiles: List<FileModel>? = null

    private lateinit var data: ProfileContactsEditItem

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
    private var onConfirmClick: ((phone : String) -> Unit)? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentFragmentManager.setFragmentResultListener(FILE_EDIT_CODE, this) {
                requestKey, result ->
        }
        parentFragmentManager.setFragmentResultListener(DEGREE_EDIT_CODE, this) {
                requestKey, result ->
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

    override fun showDisabledMainInputInfo() {
        val supportEmail = getString(R.string.support_email)
        val message = getString(R.string.profile_edit_name_disabled_message).format(supportEmail)
            .toSpannable()
        Linkify.addLinks(message, Linkify.EMAIL_ADDRESSES)

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.profile_edit_name_disabled_title)
            .setMessage(message)
            .setPositiveButton(R.string.ok, null)
            .show()
            .apply {
                findViewById<TextView>(android.R.id.message)?.let {
                    it.movementMethod = BetterLinkMovementMethod.getInstance()
                }
            }
    }

    override fun showTakePictureChooser() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.photo_alert_title)
            .setPositiveButton(R.string.photo_alert_gallery) { _, _ -> presenter.onTakePhotoFromGalleryRequest() }
            .setNegativeButton(R.string.photo_alert_camera) { _, _ -> presenter.onTakePhotoFromCameraRequest() }
            .show()
    }

    override fun changeUserAvatar(avatar: Bitmap?) {
        adapter.notifyItemChanged(0, avatar.asOptional())
    }


    override fun setPersonalData(user: UserDetail, state: String) {
        if (isUpdateInfo) {
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
                {
                    mainInfoFiles =
                        adapter.findGroupBy<GroupieViewHolder, ProfileDataAdditionalFilesEditNewGroup> {
                            true
                        }?.getCurrentFilesToSave()
                    isUpdateInfo = false
                    presenter.onAddFileClick()
                },
                { presenter.onFileClick(it) },
                { presenter.onEditFileClick(it) },
                { data, files ->
                    mainInfoFiles = files
                    isUpdateInfo = false
                }, {
                    showEditWarning(
                        presenter.getBaseUserState(),
                        presenter.getMaxUserState(),
                        false,
                        (user.binds?.recommendationFile?.size ?: 0) <= 1
                    ) {
                        presenter.onDeleteFilesClick(it)
                    }
                })

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
        isUpdateInfo = true
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
            if (item.checkDataValid()){
                if (userPhone?.isConfirmed == true) {
                    if (item.newPhoneIsConfirmed()) {
                        showEditWarning(presenter.getBaseUserState(), presenter.getMaxUserState(), item.checkBaseFieldsValid(), item.checkMaxFieldsValid()) {
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
            if (userPhone?.isConfirmed == true) {
                if (item.isNewPhoneIsValid()){
                    showEnterPassword(item.getValidatedPhone())
                }
            }else {
                presenter.onConfirmPhoneClick(item.getValidatedPhone())
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
        if (presenter.isWithUpdate()){
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
                    showPhoneConfirm(phone)
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
        PermissionsBuilder(REQUEST_GALLERY)
            .addPermissions(REQUIRED_GALLERY_PERMISSIONS)
            .setPermissionsGrantedCallback {
                val intent = Intent()
                intent.type = "*/*"
                intent.action = Intent.ACTION_GET_CONTENT
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
                galleryImage.launch(intent)
            }
            .request()
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

    override fun updateFilesList(files: List<FileModel>?) {
        files?.forEach {
            val editedName = mainInfoFiles?.firstOrNull { edFile -> edFile.uri == it.uri }
            if (editedName != null)
                it.name = editedName.name
            else
                it.name = it.name
        }
        mainInfoFiles = null
        adapter.findGroupBy<GroupieViewHolder, ProfileDataAdditionalFilesEditNewGroup> {
            true
        }?.updateFiles(files ?: emptyList())
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
        setToolbarTitleAndIcon(title)
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
}