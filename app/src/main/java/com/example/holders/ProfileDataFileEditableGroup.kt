package com.example.holders

import com.example.data.models.FileModel
import com.example.extensions.forEachGroups
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.Section


class ProfileDataFileEditableGroup(
    val files: List<FileModel>,
    private val filesCount: Int,
    private val addFileClickListener: () -> Unit,
    private val onFileClick: (file: FileModel) -> Unit,
    private val deleteFile: (file: FileModel) -> Unit
) : NestedGroup() {

    private val fileItems = arrayListOf<ProfileDataFileEditableItem>().apply {
        addAll(files.map { createFileItem(it) })
    }

    private val addFileItem = ProfileDataFileAddItem(
        filesCount < 10,
        addFileClickListener
    )
    private val fileGroup = Section()

    init {
        fileGroup.update(fileItems)

        fileGroup.registerGroupDataObserver(this)
        addFileItem.registerGroupDataObserver(this)
    }


    fun addFileItem(newFile: FileModel, fileCount: Int) {
        val fileItem = fileItems.find { x -> x.id == newFile.id?.toLong() }
        if (fileItem == null) {
            fileItems.add(createFileItem(newFile))
            fileGroup.update(fileItems)
        }
        updateButton(fileCount)
    }

    fun removeFileItem(file: FileModel, fileCount: Int) {
        val fileItem = fileItems.find { x -> x.id == file.id?.toLong() }
        if (fileItem != null) {
            fileItems.remove(fileItem)
            fileGroup.update(fileItems)
        }
        updateButton(fileCount)
    }

    fun showUploadLoading() {
        addFileItem.showLoading()
        addFileItem.notifyChanged()
    }
    fun hideUploadLoading(){
        addFileItem.hideLoading()
        addFileItem.notifyChanged()
    }

    private fun createFileItem(file: FileModel): ProfileDataFileEditableItem {
        return ProfileDataFileEditableItem(file, onFileClick) {
            deleteFile(it)
        }
    }

    private fun updateButton(fileCount: Int){
        val isEditable = fileCount < 10
        addFileItem.updateButton(isEditable)
        addFileItem.notifyChanged()
    }

    override fun getGroup(position: Int): Group {
        return when (position) {
            0 -> fileGroup
            1 -> addFileItem
            else -> throw IndexOutOfBoundsException("Invalid item position: $position")
        }
    }

    override fun getPosition(group: Group): Int {
        return when (group) {
            fileGroup -> 0
            addFileItem -> 1
            else -> -1
        }
    }

    override fun getGroupCount() = 2


    fun getCurrentFilesToSave(): List<FileModel> {
        val result = mutableListOf<FileModel>()
        fileGroup.forEachGroups<ProfileDataFileEditableItem> {
            result.add(
                FileModel(
                    id = it.file.id,
                    name = it.getFileName(),
                    showInProfile = it.file.showInProfile,
                    uri = it.file.uri,
                    mimeType = it.file.mimeType,
                    size = it.file.size,
                )
            )
        }
        return result
    }
}