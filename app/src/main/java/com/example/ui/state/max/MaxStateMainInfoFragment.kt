package com.example.ui.state.max

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.FileModel
import com.example.data.models.UserDetail
import com.example.data.models.user.User
import com.example.extensions.findGroupBy
import com.example.extensions.findItemBy
import com.example.extensions.showChangeEmailCompleteDialog
import com.example.extensions.showChangeEmailDialog
import com.example.holders.MaxStateMainInfoEditItem
import com.example.holders.ProfileDataAdditionalFilesEditNewGroup
import com.example.holders.ProfileDataFileEditItem
import com.example.holders.ProfileDataFileItem
import com.example.ui.base.BaseFragment
import com.example.ui.state.UserStateFragmentDirections
import com.example.ui.views.BaseStateDialog
import com.example.ui.views.InfoDialog
import com.example.ui.views.suggestFieldView.DaDataUtil
import com.example.util.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_register_email.*
import kotlinx.android.synthetic.main.fragment_user_edit.*
import javax.inject.Inject
import javax.inject.Provider

class MaxStateMainInfoFragment: BaseFragment(), MaxStateMainInfoContract.View {

    var mimeTypes = arrayOf("image/*", "application/pdf")
    private var isUpdateInfo = true
    private var mainInfoFiles: List<FileModel>? = null
    private var isFilesValid = false
    private var isOtherInfoValid = false
    private var filesAddedBefore = false
    private var canUpdateFields = true

    override fun layout(): Int = R.layout.fragment_max_state_info

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
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                hideKeyboard()
                navigateUp()
            }
        })
        ivClose.setOnClickListener {
            when (presenter.screen) {
                1 -> findNavController().popBackStack(R.id.profile_fragment, false)
                2 -> findNavController().popBackStack(R.id.userStateFragment, false)
            }
        }
        recyclerView.apply {
            adapter = this@MaxStateMainInfoFragment.adapter
        }

        btnSave.setOnClickListener { onSaveClick?.invoke() }
        buttonNextEnabled(false)
    }

    override fun setPersonalData(user: UserDetail) {
        if(!isGoToNextStep(user)) {
            if (canUpdateFields) filesAddedBefore = user.binds?.recommendationFile?.isNotEmpty()?: false
            val userPhone = user.phone?.firstOrNull { it.type == PHONE_PERSONAL }
            val workPhone = user.phone?.firstOrNull { it.type == PHONE_WORK }
            val dataItem = if (canUpdateFields) MaxStateMainInfoEditItem(
                    1,
                    requireContext(),
                    userPhone,
                    workPhone,
                    user.socialLinks,
                    user.site,
                    user.notes,
                    { showWhyUserShouldAddDataToNotesField() },{ isOtherInfoValid = it
                buttonNextEnabled(it)
            }) else adapter.findItemBy<GroupieViewHolder, MaxStateMainInfoEditItem> { true }

            if (!filesAddedBefore) {
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
            } else {
                isFilesValid = true
                adapter.update(listOf(dataItem))
                onSaveClick = {
                    recyclerView.requestFocus()
                    val dataToSave = dataItem?.getDataToSave() as MutableMap
                    presenter.updateFiles(dataToSave)
                }
            }
        } else {
            findNavController().navigate(MaxStateMainInfoFragmentDirections.actionMaxStateMainInfoFragmentToBaseStateInterestsFragment().setScreen(presenter.screen))
        }
        buttonNextEnabled(isFilesValid)
        canUpdateFields = false
    }

    private fun buttonNextEnabled(enabled: Boolean) {
        btnSave.isEnabled = isFilesValid && isOtherInfoValid
    }

    private fun isGoToNextStep(user: UserDetail): Boolean {
        var isGoToNex = true
        if (user.binds?.recommendationFile?.isEmpty() == true) isGoToNex = false
        if (user.phone?.firstOrNull { it.type == PHONE_WORK }?.value == null) isGoToNex = false
        if (user.socialLinks?.value?.isEmpty() == true) isGoToNex = false
        if (user.site?.value?.isEmpty() == true) isGoToNex = false
        if (user.email?.value.isNullOrEmpty()) isGoToNex = false
        if (user.notes.isNullOrEmpty()) isGoToNex = false
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
        adapter.update(listOf(
                editItem,
                ProfileDataFileItem(file.name ?: "") { presenter.onFileClick(file) },
                /*ProfileButtonEditItem(getString(R.string.add_file), true) { presenter.onFileEditSaveClick() }.apply {
                    hasDivider = false
                    compactMargin = true
                }*/
        ))

        onSaveClick = {
            hideKeyboard()
            /*file.desc = editItem.mName
            file.newName = editItem.mName*/
            presenter.onSaveFileClick(mutableMapOf(User.FIELD_ATTACHED_FILES to file))
        }
    }

    override fun saveOnClick(saveOnClick: Boolean) {
        btnSave.isVisible = saveOnClick
    }

    private fun showWhyUserShouldAddDataToNotesField() {
        InfoDialog(requireContext(), getString(R.string.profile_edit_additional_notes_data), requireActivity())
                .setSelectCallback {  }
    }

    override fun goToNext() {
        when (Utils.maxStateScreen(presenter.getUserData())) {
            MaxStateScreenType.WORK ->
                findNavController().navigate(MaxStateMainInfoFragmentDirections.actionMaxStateMainInfoFragmentToMaxStateWorkFragment().setScreen(presenter.screen))
            MaxStateScreenType.EDUCATION ->
                findNavController().navigate(MaxStateMainInfoFragmentDirections.actionMaxStateMainInfoFragmentToMaxStateEducationFragment().setScreen(presenter.screen))
            MaxStateScreenType.DONE -> BaseStateDialog(resources.getString(R.string.you_got_max_state), requireActivity())
                    .setSelectCallback {
                        when (presenter.screen) {
                            1 -> findNavController().popBackStack(R.id.profile_fragment, false)
                            2 -> findNavController().popBackStack(R.id.userStateFragment, false)
                        }
                    }
            else ->
                findNavController().navigate(MaxStateMainInfoFragmentDirections.actionMaxStateMainInfoFragmentToBaseStateInterestsFragment().setScreen(presenter.screen))
        }
    }

    override fun showUpdateError(message: String?) {
        val title = getString(R.string.profile_edit_request_error)
        Toast.makeText(requireContext(), message?.let { "$title: $it" }
                ?: title, Toast.LENGTH_SHORT).show()
    }

    override fun showChangeEmail() = showChangeEmailDialog(presenter::onChangeEmailConfirm)

    override fun showChangeEmailComplete(email: String) = showChangeEmailCompleteDialog(email)
}