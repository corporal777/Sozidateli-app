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
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.text.toSpannable
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Interest
import com.example.data.models.UserAddress
import com.example.data.models.UserInterest
import com.example.data.models.asOptional
import com.example.data.models.user.RecommendationFile
import com.example.data.models.user.User
import com.example.extensions.findGroupBy
import com.example.extensions.showChangeEmailCompleteDialog
import com.example.extensions.showChangeEmailDialog
import com.example.holders.*
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.userprofile.academicdegree.EditDegreeFragment.Companion.DEGREES_LEVEL
import com.example.ui.userprofile.academicdegree.EditDegreeFragment.Companion.DEGREE_EDIT_CODE
import com.example.ui.userprofile.academicdegree.EditDegreeFragment.Companion.ITEM_POSITION
import com.example.ui.userprofile.academicdegree.EditDegreeFragment.Companion.SCIENCES_LEVEL
import com.example.ui.userprofile.editfile.UserEditFileFragment.Companion.FILE_EDIT_CODE
import com.example.ui.userprofile.editfile.UserEditFileFragment.Companion.FILE_PATH
import com.example.ui.views.InfoDialog
import com.example.ui.views.suggestFieldView.DaDataUtil
import com.example.ui.views.toolbar.ToolbarContentActionBar
import com.example.util.*
import com.vincent.filepicker.Constant
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_user_edit.*
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import javax.inject.Inject
import javax.inject.Provider

class UserEditFragment : BaseFragment(), UserEditContract.View, ToolbarFragment {

    var mimeTypes = arrayOf("image/*", "application/pdf")
    private var isUpdateInfo = true
    private var mainInfoFiles: List<RecommendationFile>? = null

    override val title: String? = null

    override fun layout() = R.layout.fragment_user_edit

    @InjectPresenter
    lateinit var presenter: UserEditPresenter

    @Inject
    lateinit var presenterProvider: Provider<UserEditPresenter>

    @ProvidePresenter
    fun providePresenter(): UserEditPresenter = presenterProvider.get().apply {
        editType = UserEditFragmentArgs.fromBundle(requireArguments()).type
    }

    private val galleryImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { uri ->
        uri?.let {
            it.data?.data?.let {  file ->
                val filePath = FileUtils.getPath(requireContext(), file)
                val mimeType = FileUtils.getMimeType(requireContext(), file)
                if (filePath.isEmpty()) {
                    val path = UriUtils.pickedExistingPicture(requireContext(), file).path
                    val type = UriUtils.getMimeType(requireContext(), file)?: ""
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
            (recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position, 0)
        }
    }

    private var onSaveClick: (() -> Unit)? = null

    private lateinit var toolbarContentActionBar: ToolbarContentActionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentFragmentManager.setFragmentResultListener(FILE_EDIT_CODE, this,
                FragmentResultListener { requestKey, result ->
                    val file = result.getParcelable<RecommendationFile>(FILE_PATH)
                    presenter.onSaveFileClick(mapOf(User.FIELD_ATTACHED_FILES to file))
                })
        parentFragmentManager.setFragmentResultListener(DEGREE_EDIT_CODE, this,
                FragmentResultListener { requestKey, result ->
                    val degreesLevel = result.getString(DEGREES_LEVEL)
                    val sciencesLevel = result.getString(SCIENCES_LEVEL)
                    val position = result.getInt(ITEM_POSITION)
                    (adapter.getGroup(1) as ProfileDataEducationEditGroup).addDegree(degreesLevel, sciencesLevel, position)
                })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                hideKeyboard()
                presenter.onNavigateUpRequest()
            }
        })

        recyclerView.apply {
            adapter = this@UserEditFragment.adapter
        }

        btnSave.setOnClickListener { onSaveClick?.invoke() }
    }

    override fun setMainData(user: User, avatar: Bitmap?) {
        val dataItem = ProfileDataUserEditItem(
                avatar,
                user.user_name,
                user.user_last_name,
                user.user_middle_name,
                { presenter.onRemoveAvatarClick() },
                { presenter.onEditAvatarClick() },
                { presenter.onDisabledMainInputInfoClick() }
        )
        adapter.update(listOf(dataItem))

        onSaveClick = {
            if (dataItem.checkDataComplete()) {
                presenter.onSaveMainClick(dataItem.getDataToSave())
            }
        }
    }

    override fun showDisabledMainInputInfo() {
        val supportEmail = getString(R.string.support_email)
        val message = getString(R.string.profile_edit_name_disabled_message).format(supportEmail).toSpannable()
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

    override fun setPersonalData(user: User) {
        val dataItem = ProfileDataPersonalEditItem(
                requireContext(),
                user.user_email,
                user.user_email_show,
                user.user_phone_work,
                user.user_phone_work_show,
                user.user_phone,
                user.user_phone_show,
                user.user_phone_confirmed,
                user.user_gender?.firstLetterToUppercase(),
                user.user_birthday,
                user.user_birthday_show,
                UserAddress.fromUser(user),
                user.social_links,
                { presenter.onChangeEmailClick() },
                { presenter.onConfirmPhoneClick(it) }
        )

        adapter.update(listOf(dataItem))

        onSaveClick = {
            recyclerView.requestFocus()
            if (dataItem.checkDataValid()) {
                presenter.onSavePersonalClick(dataItem.getDataToSave())
            }
        }
    }

    override fun setPersonalDataNew(user: User) {
        if (isUpdateInfo) {
            val dataItem = ProfileDataPersonalEditNewItem(
                    1,
                    requireContext(),
                    user.user_name,
                    user.user_last_name,
                    user.user_middle_name,
                    user.user_gender?.firstLetterToUppercase(),
                    user.user_birthday,
                    user.user_birthday_show,
                    DaDataUtil.formatSavedLocation(requireContext(), UserAddress.fromUser(user)),
                    user.user_notes,
                    childFragmentManager) { showWhyUserShouldAddDataToNotesField() }

            user.attached_recomendation_files?.forEach {
                it.newName = (if (it.desc.isNullOrBlank()) it.name else it.desc) ?: "file"
            }

            val files = ProfileDataAdditionalFilesEditNewGroup(
                    2,
                    requireContext(),
                    user.attached_recomendation_files ?: emptyList(),
                    {
                        mainInfoFiles = adapter.findGroupBy<GroupieViewHolder, ProfileDataAdditionalFilesEditNewGroup> {
                            true
                        }?.getCurrentFilesToSave()
                        isUpdateInfo = false
                        presenter.onAddFileClick()
                    },
                    {
                        presenter.onFileClick(it)
                    },
                    {
                        presenter.onEditFileClick(it)
                    },
                    { data, files ->
                        mainInfoFiles = files
                        isUpdateInfo = false
                        presenter.onSaveAdditionalFilesClick(data)
                    }
            )

            adapter.update(listOf(dataItem, files))

            onSaveClick = {
                recyclerView.requestFocus()
                if (dataItem.checkDataValid()) {
                    val dataToSave = dataItem.getDataToSave() as MutableMap
                    val file = files.getCurrentFilesToSave()
                    file.forEach { f ->
                        if (f.name != f.newName)
                            f.name = f.newName
                    }
                    dataToSave[User.FIELD_ATTACHED_FILES] = file
                    presenter.onSavePersonalClick(dataToSave)
                }
            }
        }
        isUpdateInfo = true
    }

    override fun setContactsData(user: User) {
        val item = ProfileContactsEditItem(
                requireContext(),
                user.user_phone,
                user.user_phone_show,
                user.user_phone_confirmed,
                user.user_phone_work,
                user.user_phone_work_show,
                user.social_links,
                user.site,
                user.user_email,
                user.user_email_show,
                user.user_site_absent,
                user.user_social_links_absent,
                user.user_work_phone_absent,
                presenter::onChangeEmailClick,
                presenter::onConfirmPhoneClick
        )

        adapter.update(listOf(item))

        onSaveClick = {
            recyclerView.requestFocus()
            if (item.checkDataValid()) {
                presenter.onSaveContactsClick(item.getDataToSave())
            }
        }
    }

    override fun setPhoneData(user: User) {
        val item = ProfilePhoneEditItem(
                requireContext(),
                user.user_phone,
                user.user_phone_show,
                user.user_phone_confirmed,
                presenter::onConfirmPhoneClick
        )

        adapter.update(listOf(item))

        onSaveClick = {
            recyclerView.requestFocus()
            if (item.checkDataValid()) {
                presenter.onSaveContactsClick(item.getDataToSave())
            }
        }
    }

    override fun updateFilesList(files: List<RecommendationFile>?) {
        files?.forEach {
            val editedName = mainInfoFiles?.firstOrNull { edFile -> edFile.url == it.url }
            if (editedName != null)
                it.newName = editedName.newName
            else
                it.newName = it.name
        }
        mainInfoFiles = null
        adapter.findGroupBy<GroupieViewHolder, ProfileDataAdditionalFilesEditNewGroup> {
            true
        }?.updateFiles(files ?: emptyList())
    }

    override fun showChangeEmail() = showChangeEmailDialog(presenter::onChangeEmailConfirm)

    override fun showChangeEmailComplete(email: String) = showChangeEmailCompleteDialog(email)

    override fun showPhoneConfirm(phone: String) {
        findNavController().navigate(UserEditFragmentDirections.editToPhoneConfirm(phone))
    }

    override fun showUpdateError(message: String?) {
        val title = getString(R.string.profile_edit_request_error)
        Toast.makeText(requireContext(), message?.let { "$title: $it" }
                ?: title, Toast.LENGTH_SHORT).show()
    }

    override fun setEducationData(user: User) {
        val academicDegree = if (user.academic_degree?.size == 1 && user.academic_degree?.get(0)?.degree == "")
            null else user.academic_degree
        val dataItem = ProfileDataEducationEditGroup(
                requireContext(),
                user.user_birthday,
                user.user_education,
                user.available_education ?: emptyList(),
                user.available_degrees ?: emptyList(),
                user.available_sciences ?: emptyList(),
                user.education ?: emptyList(),
                /*user.academic_degree ?: emptyList()*/academicDegree ?: emptyList()
        ) { degreesLevel, sciencesLevel, position ->
            findNavController().navigate(UserEditFragmentDirections.actionUserEditFragmentToEditDegreeFragment(degreesLevel, sciencesLevel, position))
        }
        adapter.update(listOf(dataItem))

        onSaveClick = {
            if (dataItem.checkDataValid()) {
                presenter.onSaveEducationClick(dataItem.getDataToSave())
            }
        }
    }

    override fun setWorkData(user: User) {
        val work = user.work ?: emptyList()
        val dataItem = ProfileDataWorkEditGroup(
                requireContext(),
                user.user_birthday,
                work,
                user.user_work_experience_absent
        ) { presenter.onSaveWorkClick(mapOf(User.FIELD_USER_HAS_WORK_EXPERIENCE to it)) }
        adapter.update(listOf(dataItem))
        onSaveClick = {
            if (dataItem.checkDataValid()) {
                presenter.onSaveWorkClick(dataItem.getDataToSave())
            }
        }
    }

    override fun setInterestsData(interests: Map<Interest, List<UserInterest>>) {
        val findUserInterests: () -> List<Interest> = {
            interests.values.flatten().filter { item -> item.isUserInterest }
                    .map { item -> item.interest }
        }

        var userInterests = findUserInterests()

        adapter.update(interests.map {
            val parent = it.key
            val childList = it.value
            ProfileExpandableSubtitleGroup(parent.value, onExpandChange = onItemExpandChange).apply {
                titleItem.badgeCount = childList.count { child -> child.isUserInterest }
                val interestsItems = childList.mapIndexed { index, interest ->
                    ProfileDataInterestEditItem(interest, index != childList.size - 1) {
                        userInterests = findUserInterests()
                        val count = childList.count { child -> child.isUserInterest }
                        titleItem.apply {
                            badgeCount = count
                            notifyChanged(count)
                        }
                    }
                }
                addAll(interestsItems)
            }
        })

        onSaveClick = { presenter.onSaveInterestsClick(userInterests) }
    }

    override fun setAdditionalNotesData(user: User) {
        val dataItem = ProfileDataNotesEditItem(1L, user.user_notes)
        adapter.update(listOf(ProfileDataNotesDescriptionItem(0L) { showWhyUserShouldAddDataToNotesField() }, dataItem))
        onSaveClick = { presenter.onSaveAdditionalNotesClick(dataItem.mNotes) }
    }

    private fun showWhyUserShouldAddDataToNotesField() {
        InfoDialog(requireContext(), getString(R.string.profile_edit_additional_notes_data), requireActivity())
                .setSelectCallback {  }
    }

    override fun setAdditionalFilesData(user: User) {
        adapter.update(listOf(ProfileDataAdditionalFilesEditGroup(
                requireContext(),
                user.attached_recomendation_files ?: emptyList(),
                { presenter.onAddFileClick() },
                { presenter.onFileClick(it) },
                { presenter.onEditFileClick(it) },
                { presenter.onSaveAdditionalFilesClick(it) }
        )))
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

    override fun setFileEditData(file: RecommendationFile) {
        val editItem = ProfileDataFileEditItem(file.desc)
        adapter.update(listOf(
                editItem,
                ProfileDataFileItem(file.name ?: "") { presenter.onFileClick(file) },
                ProfileButtonEditItem(getString(R.string.add_file), true) { presenter.onFileEditSaveClick() }.apply {
                    hasDivider = false
                    compactMargin = true
                }
        ))

        onSaveClick = {
            hideKeyboard()
            file.desc = editItem.mName
            presenter.onFileEditSaveClick()
        }
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
                /*val file = result?.getParcelableExtra<NormalFile>(Constant.RESULT_PICK_FILE)
                file?.let {
                    presenter.onFilePicked(it.path)
                }*/
            }
        }
    }

    override fun setMainTitle() = setTitle(R.string.profile_edit_name_and_photo)
    override fun setPersonalTitle() = setTitle(R.string.user_profile_main_info)
    override fun setContactsTitle() = setTitle(R.string.user_profile_contacts)
    override fun setPhoneTitle() = setTitle(R.string.profile_phone_mobile)
    override fun setEducationTitle() = setTitle(R.string.profile_title_education)
    override fun setWorkTitle() = setTitle(R.string.profile_work_experience)
    override fun setInterestsTitle() = setTitle(R.string.profile_interests)
    override fun setAdditionalNotesTitle() = setTitle(R.string.profile_notes)
    override fun setAdditionalFilesTitle() = setTitle(R.string.profile_files_title)

    private fun setTitle(@StringRes titleRes: Int) {
        toolbarContentActionBar.setTitle(titleRes)
    }

    override fun setupToolbarContent(toolbarContentActionBar: ToolbarContentActionBar) {
        super.setupToolbarContent(toolbarContentActionBar)
        this.toolbarContentActionBar = toolbarContentActionBar
    }

    override fun navigateUp() {
        presenter.onNavigateUpRequest()
    }

    override fun navigateUpChecked() {
        super.navigateUp()
    }

    override fun saveOnClick(saveOnClick: Boolean) {
        btnSave.isVisible = saveOnClick
    }
}