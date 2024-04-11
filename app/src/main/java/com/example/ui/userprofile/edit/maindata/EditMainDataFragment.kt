package com.example.ui.userprofile.edit.maindata

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import com.example.R
import com.example.data.models.*
import com.example.extensions.findGroupBy
import com.example.extensions.updateItem
import com.example.holders.*
import com.example.ui.userprofile.base.BaseUserProfileEditFragment
import com.example.ui.views.suggestFieldView.address.DaDataUtil
import com.example.util.FileUtils
import com.example.util.UriUtils
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class EditMainDataFragment : BaseUserProfileEditFragment(), EditMainDataContract.View {

    @InjectPresenter
    lateinit var presenter: EditMainDataPresenter

    @Inject
    lateinit var presenterProvider: Provider<EditMainDataPresenter>

    @ProvidePresenter
    fun providePresenter(): EditMainDataPresenter = presenterProvider.get()

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


    override fun setPlaceholder() {
        groupAdapter.updateItem(PlaceholderItem(PlaceholderItem.Type.CONTACTS))
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

        groupAdapter.update(listOf(dataItem, files))

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


    override fun showFileSelector() {
        val intent = Intent()
        intent.type = "*/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "application/pdf"))
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
        groupAdapter.findGroupBy<ProfileDataFileEditableGroup> { true }?.addFileItem(file, fileCount)
    }

    override fun deleteUserFile(file: FileModel, fileCount: Int) {
        groupAdapter.findGroupBy<ProfileDataFileEditableGroup> { true }?.removeFileItem(file, fileCount)
    }

    override fun showFileUploadLoading() {
        groupAdapter.findGroupBy<ProfileDataFileEditableGroup> { true }?.showUploadLoading()
    }

    override fun hideFileUploadLoading() {
        groupAdapter.findGroupBy<ProfileDataFileEditableGroup> { true }?.hideUploadLoading()
    }



    override val title: CharSequence by lazy { getString(R.string.user_profile_main_info) }
}