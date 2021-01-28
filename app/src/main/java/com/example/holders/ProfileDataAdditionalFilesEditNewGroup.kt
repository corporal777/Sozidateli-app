package com.example.holders

import android.content.Context
import com.example.R
import com.example.data.models.user.RecommendationFile
import com.example.data.models.user.User
import com.example.extensions.forEachGroups
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.Section

class ProfileDataAdditionalFilesEditNewGroup(
        private val id: Long,
        context: Context,
        files: List<RecommendationFile>,
        addFileClickListener: () -> Unit,
        private val onFileClick: (RecommendationFile) -> Unit,
        private val onFileEditClick: (RecommendationFile) -> Unit,
        private val saveClickListener: (data: MutableMap<String, Any?>, files: List<RecommendationFile>) -> Unit
) : NestedGroup() {

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

    fun updateFiles(files: List<RecommendationFile>) {
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

    private fun createFileItem(id: Long, file: RecommendationFile): ProfileDataFileEditableItemNew {
        return ProfileDataFileEditableItemNew(
                id,
                file,
                onFileClick,
                onFileEditClick,
                { item ->
                    saveClickListener(mutableMapOf(
                            User.FIELD_ATTACHED_FILES to fileItems.filter { item != it }.map { it.file }
                    ), getCurrentFilesToSave())
                }
        )
    }

    fun getCurrentFilesToSave(): List<RecommendationFile> {
        val result = mutableListOf<RecommendationFile>()
        fileGroup.forEachGroups<ProfileDataFileEditableItemNew> {
            result.add(RecommendationFile(id = it.file.id, type = it.file.type,
            name = it.file.name, desc = it.file.desc, url = it.file.url, newName = it.file.newName))
        }
        return result
    }
}