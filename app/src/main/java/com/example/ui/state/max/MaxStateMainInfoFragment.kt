package com.example.ui.state.max

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.FileModel
import com.example.data.models.ImageModel
import com.example.data.models.UserDetail
import com.example.data.models.user.User
import com.example.databinding.FragmentMaxStateBinding
import com.example.databinding.FragmentMaxStateInfoBinding
import com.example.extensions.findGroupBy
import com.example.extensions.findItemBy
import com.example.extensions.showChangeEmailCompleteDialog
import com.example.extensions.showChangeEmailDialog
import com.example.holders.MaxStateMainInfoEditItem
import com.example.holders.ProfileDataAdditionalFilesEditNewGroup
import com.example.holders.ProfileDataFileEditItem
import com.example.holders.ProfileDataFileItem
import com.example.ui.base.BaseFragment
import com.example.ui.base.BaseFragmentNew
import com.example.ui.views.InfoDialog
import com.example.ui.views.dialogs_new.MessageDialogWithBrownButton
import com.example.ui.views.toolbar.SimpleTitleToolbar
import com.example.util.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import onScrolled
import javax.inject.Inject
import javax.inject.Provider

class MaxStateMainInfoFragment : BaseFragmentNew<FragmentMaxStateInfoBinding>(),
    MaxStateMainInfoContract.View, SimpleTitleToolbar {

    var mimeTypes = arrayOf("image/*", "application/pdf")
    private var isUpdateInfo = true
    private var mainInfoFiles: List<FileModel>? = null
    private var isFilesValid = false
    private var isOtherInfoValid = false
    private var filesAddedBefore = false
    private var canUpdateFields = true

    override fun layout(): Int = R.layout.fragment_max_state_info

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
        setToolbarTitle()
        mBinding.recyclerView.apply {
            adapter = this@MaxStateMainInfoFragment.adapter
            onScrolled { _, _ ->
                presenter.changeAppBarElevation(this.computeVerticalScrollOffset())
            }
        }

        mBinding.btnSave.setOnClickListener { onSaveClick?.invoke() }
        buttonNextEnabled(false)
    }

    override fun setPersonalData(user: UserDetail) {
        if (!isGoToNextStep(user)) {
            if (canUpdateFields) filesAddedBefore =
                user.binds?.recommendationFile?.isNotEmpty() ?: false
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
                }, {
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
                }) else adapter.findItemBy<GroupieViewHolder, MaxStateMainInfoEditItem> { true }

            /*if (!filesAddedBefore) {
                isFilesValid = user.binds?.recommendationFile?.isNotEmpty()?: false
                val files = ProfileDataAdditionalFilesEditNewGroup(
                        2,
                        requireContext(),
                        user.binds?.recommendationFile ?: emptyList(),
                        {
                            mainInfoFiles = adapter.findGroupBy<GroupieViewHolder, ProfileDataAdditionalFilesEditNewGroup> {
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
                            presenter.onSaveAdditionalFilesClick(data)
                        }, { presenter.onDeleteFilesClick(it) }
                )

                adapter.update(listOf(dataItem, files))
                onSaveClick = {
                    recyclerView.requestFocus()
                    if (dataItem?.checkDataValid() == true) {
                        val dataToSave = dataItem.getDataToSave() as MutableMap
                        val file = files.getCurrentFilesToSave()
                        presenter.updateFiles(file.toMutableList(), dataToSave)
                    }
                }
            } else {*/
            isFilesValid = true
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
        buttonNextEnabled(isFilesValid)
        canUpdateFields = false
    }

    override fun photoUpdated(photo: ImageModel) {
        adapter.findItemBy<GroupieViewHolder, MaxStateMainInfoEditItem> { true }?.setImage(photo)
    }

    private fun buttonNextEnabled(enabled: Boolean) {
        mBinding.btnSave.isEnabled = isFilesValid && isOtherInfoValid
    }

    private fun isGoToNextStep(user: UserDetail): Boolean {
        var isGoToNex = true
        if (user.binds?.recommendationFile?.isEmpty() == true) isGoToNex = false
        if (user.phone?.firstOrNull { it.type == PHONE_WORK }?.value == null) isGoToNex = false
        if (user.contactInformation.socialLinks?.values?.isEmpty() == true) isGoToNex = false
        if (user.contactInformation.site?.values?.isEmpty() == true) isGoToNex = false
        if (user.email?.value.isNullOrEmpty()) isGoToNex = false
        if (user.notes?.value.isNullOrEmpty()) isGoToNex = false
        //if (user.image.uri.isNullOrEmpty()) isGoToNex = false
        return isGoToNex
    }

    override fun updateFilesList(files: List<FileModel>?) {
        isFilesValid = true
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
        buttonNextEnabled(isFilesValid)
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

    override fun setFileEditData(file: FileModel) {
        val editItem = ProfileDataFileEditItem(file.name)
        adapter.update(
            listOf(
                editItem,
                ProfileDataFileItem(file.name ?: "") { presenter.onFileClick(file) },
            )
        )

        onSaveClick = {
            hideKeyboard()
            presenter.onSaveFileClick(mutableMapOf(User.FIELD_ATTACHED_FILES to file))
        }
    }

    override fun saveOnClick(saveOnClick: Boolean) {
        mBinding.btnSave.isVisible = saveOnClick
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

    private fun setToolbarTitle() {
        val actionIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_close_new)
        setToolbarTitleAndIcon(
            getString(R.string.user_profile_contacts),
            actionIcon,
            action = {
                presenter.onClickClose()
            })
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
}