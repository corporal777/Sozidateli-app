package com.example.holders

import android.content.Context
import com.example.R
import com.example.data.models.FileModel
import com.example.extensions.forEachGroups
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.Section

class ProfileDataAdditionalFilesEditNewGroup(
    private val id: Long,
    context: Context,
    files: List<FileModel>,
    addFileClickListener: () -> Unit,
    private val onFileClick: (FileModel) -> Unit,
    private val deleteFile: (data: FileModel) -> Unit
) : NestedGroup() {

    private val fileGroup = Section().apply {
        setHeader(ProfileDataAdditionalFileHeaderItem(id))
        setFooter(
            ProfileButtonEditItem(
                id + 1,
                context.getString(R.string.add_file),
                true,
                addFileClickListener
            ).apply {
                hasDivider = false
                compactMargin = true
            }
        )
    }

    private val fileItems = mutableListOf<ProfileDataFileEditableItemNew>().apply {
        addAll(files.map { file -> createFileItem(file) })
    }

    init {
        fileGroup.update(fileItems)
        add(fileGroup)
    }

    fun updateFiles(files: List<FileModel>) {
        fileItems.clear()
        fileItems.addAll(files.mapIndexed { index, file -> createFileItem(file) })
        fileGroup.update(fileItems)
    }

    fun addNewFile(newFile : FileModel){
        fileItems.add(createFileItem(newFile))
        fileGroup.update(fileItems)
    }

    fun deleteUserFile(file : FileModel) {
        val foundItem = fileItems.find { x -> x.file.id == file.id }
        if (foundItem != null){
            fileItems.remove(foundItem)
            fileGroup.update(fileItems)
        }
    }

    override fun getGroup(position: Int): Group {
        return when (position) {
            0 -> fileGroup
            else -> throw IndexOutOfBoundsException("Invalid item position: $position")
        }
    }

    override fun getPosition(group: Group): Int {
        return when (group) {
            fileGroup -> 0
            else -> -1
        }
    }

    override fun getGroupCount(): Int {
        return 1
    }

    private fun createFileItem(file: FileModel): ProfileDataFileEditableItemNew {
        return ProfileDataFileEditableItemNew(
            file,
            onFileClick
        ) { item ->
            deleteFile(item.file)
        }
    }

    fun getCurrentFilesToSave(): List<FileModel> {
        val result = mutableListOf<FileModel>()
        fileGroup.forEachGroups<ProfileDataFileEditableItemNew> { result.add(it.file) }
        return result
    }
}