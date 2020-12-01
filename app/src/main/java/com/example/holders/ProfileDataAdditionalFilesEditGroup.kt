package com.example.holders

import android.content.Context
import com.example.R
import com.example.data.models.user.RecommendationFile
import com.example.data.models.user.User
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.Section

class ProfileDataAdditionalFilesEditGroup(
        context: Context,
        files: List<RecommendationFile>,
        addFileClickListener: () -> Unit,
        private val onFileClick: (RecommendationFile) -> Unit,
        private val onFileEditClick: (RecommendationFile) -> Unit,
        private val saveClickListener: (data: Map<String, Any?>) -> Unit
) : NestedGroup() {

    private val fileGroup = Section().apply {
//        setHeader(ProfileDataAdditionalFileHeaderItem(ID_FILES))
    }
    private val addItem = ProfileButtonEditItem(ID_ADD, context.getString(R.string.add_file), true, addFileClickListener).apply {
        hasDivider = false
        compactMargin = true
    }

    private val fileItems = mutableListOf<ProfileDataFileEditableItem>()

    init {
        fileItems.addAll(files.map { createFileItem(it) })
        add(fileGroup.apply { addAll(fileItems) })
        add(addItem)
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

    private fun createFileItem(file: RecommendationFile): ProfileDataFileEditableItem {
        return ProfileDataFileEditableItem(
                ID_FILE + file.id,
                file,
                onFileClick,
                onFileEditClick,
                { item ->
                    saveClickListener(mapOf(
                            User.FIELD_ATTACHED_FILES to fileItems.filter { item != it }.map { it.file }
                    ))
                }
        )
    }

    companion object {
        private const val ID_ADD = 3L
        private const val ID_FILES = 3L
        private const val ID_FILE = 4L
    }
}