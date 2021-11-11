package com.example.holders

import android.content.Context
import com.example.R
import com.example.data.models.FileModel
import com.example.data.models.user.RecommendationFile
import com.example.data.models.user.User
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
        private val onFileEditClick: (FileModel) -> Unit,
        private val saveClickListener: (data: MutableMap<String, Any?>, files: List<FileModel>) -> Unit,
        private val deleteFile: (data: FileModel) -> Unit
) : NestedGroup() {

    private var filesSave = files.map { FileModel(id = it.id, user = it.user, mimeType = it.mimeType, size = it.size, name = it.name, uri = it.uri, showInProfile = it.showInProfile) }

    private val fileGroup = Section().apply {
        setHeader(ProfileDataAdditionalFileHeaderItem(id))
    }
    private val addItem = ProfileButtonEditItem(id + 1, context.getString(R.string.add_file), true, addFileClickListener).apply {
        hasDivider = false
        compactMargin = true
    }

    private val fileItems = mutableListOf<ProfileDataFileEditableItemNew>()

    init {
        updateFiles(files)
        add(fileGroup)
        add(addItem)
    }

    fun updateFiles(files: List<FileModel>) {
        fileItems.clear()
        fileItems.addAll(files.mapIndexed { index, file -> createFileItem(id + 2 + index, file) })
        fileGroup.update(fileItems)
    }

    override fun getGroup(position: Int): Group {
        return when (position) {
            0 -> fileGroup
            1 -> addItem
            else -> throw IndexOutOfBoundsException("Invalid item position: $position")
        }
    }

    override fun getPosition(group: Group): Int {
        return when (group) {
            fileGroup -> 0
            addItem -> 1
            else -> -1
        }
    }

    override fun getGroupCount(): Int {
        return 2
    }

    private fun createFileItem(id: Long, file: FileModel): ProfileDataFileEditableItemNew {
        return ProfileDataFileEditableItemNew(
                id,
                file,
                onFileClick,
                onFileEditClick,
                { item ->
                    deleteFile(item.file)
                    /*saveClickListener(mutableMapOf(
                            User.FIELD_ATTACHED_FILES to fileItems.filter { item != it }.map { it.file }
                    ), getCurrentFilesToSave())*/
                }
        )
    }

    fun getCurrentFilesToSave(): List<FileModel> {
        val result = mutableListOf<FileModel>()
        fileGroup.forEachGroups<ProfileDataFileEditableItemNew> {
            val f = filesSave.firstOrNull { file -> file.id == it.file.id }
            if ((f != null && f.name != it.file.name) || (f != null && f?.showInProfile != it.file.showInProfile))
                result.add(FileModel(id = it.file.id, name = it.file.name, user = it.file.user, mimeType = it.file.mimeType, size = it.file.size, uri = it.file.uri, showInProfile = it.file.showInProfile))
        }
        return result
    }
}