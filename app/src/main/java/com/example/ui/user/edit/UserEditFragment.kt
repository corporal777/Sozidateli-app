package com.example.ui.user.edit

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.text.util.Linkify
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.text.toSpannable
import androidx.core.view.isVisible
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
import com.example.holders.*
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.views.toolbar.ToolbarContentActionBar
import com.example.util.AuthValidateUtil
import com.google.android.material.textfield.TextInputLayout
import com.vincent.filepicker.Constant
import com.vincent.filepicker.activity.PDFFilePickActivity
import com.vincent.filepicker.filter.entity.NormalFile
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_user_edit.*
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import javax.inject.Inject
import javax.inject.Provider

class UserEditFragment : BaseFragment(), UserEditContract.View, ToolbarFragment {

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

    private val adapter = GroupAdapter<GroupieViewHolder>()

    private val onItemExpandChange: OnExpandChange<*> = {
        if (it.isExpanded) {
            val position = adapter.getAdapterPosition(it.titleItem)
            (recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position, 0)
        }
    }

    private var onSaveClick: (() -> Unit)? = null

    private lateinit var toolbarContentActionBar: ToolbarContentActionBar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
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
                user.user_gender,
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

    override fun showChangeEmail() {
        val view = layoutInflater.inflate(R.layout.dialog_change_email, null)
        val til = view.findViewById<TextInputLayout>(R.id.tilEmail)
        val et = view.findViewById<EditText>(R.id.etEmail)
        AlertDialog.Builder(requireContext())
                .setTitle(R.string.profile_email_change)
                .setView(view)
                .setPositiveButton(R.string.ok, null)
                .setNegativeButton(R.string.cancel, null)
                .create()
                .apply {
                    setOnShowListener {
                        getButton(AlertDialog.BUTTON_POSITIVE).apply {
                            setOnClickListener {
                                val email = et.text.toString()
                                if (AuthValidateUtil.isValidEmail(email)) {
                                    presenter.onChangeEmailConfirm(email)
                                    dismiss()
                                } else til.error = getString(R.string.profile_edit_email_invalid)
                            }
                        }
                    }
                }
                .show()
    }

    override fun showChangeEmailComplete(email: String) {
        AlertDialog.Builder(requireContext())
                .setTitle(R.string.email_change_title)
                .setMessage(String.format(getString(R.string.email_change_msg, email)))
                .setPositiveButton(R.string.ok, null)
                .show()
    }

    override fun showPhoneConfirm(phone: String) {
        findNavController().navigate(UserEditFragmentDirections.editToPhoneConfirm(phone))
    }

    override fun showUpdateError(message: String?) {
        val title = getString(R.string.profile_edit_request_error)
        Toast.makeText(requireContext(), message?.let { "$title: $it" }
                ?: title, Toast.LENGTH_SHORT).show()
    }

    override fun setEducationData(user: User) {
        val dataItem = ProfileDataEducationEditGroup(
                requireContext(),
                user.user_education,
                user.available_education ?: emptyList(),
                user.available_degrees ?: emptyList(),
                user.available_sciences ?: emptyList(),
                user.education ?: emptyList(),
                user.academic_degree ?: emptyList()
        )
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
                work
        )
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
        AlertDialog.Builder(requireContext())
                .setMessage(R.string.profile_edit_additional_notes_data)
                .setPositiveButton(R.string.ok, null)
                .show()
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
        startActivityForResult(Intent(requireContext(), PDFFilePickActivity::class.java), Constant.REQUEST_CODE_PICK_FILE)
    }

    override fun setFileEditData(file: RecommendationFile) {
        val editItem = ProfileDataFileEditItem(file.desc)
        adapter.update(listOf(
                editItem,
                ProfileDataFileItem(file.name ?: "") { presenter.onFileClick(file) },
                ProfileButtonEditItem(getString(R.string.add_file)) { presenter.onFileEditSaveClick() }.apply {
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
                val file = result?.getParcelableExtra<NormalFile>(Constant.RESULT_PICK_FILE)
                file?.let {
                    presenter.onFilePicked(it.path)
                }
            }
        }
    }

    override fun setMainTitle() = setTitle(R.string.profile_edit_name_and_photo)
    override fun setPersonalTitle() = setTitle(R.string.profile_contacts)
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