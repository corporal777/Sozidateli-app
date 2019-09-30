package com.example.holders

import android.content.Context
import com.example.R
import com.example.data.models.user.RecommendationFile
import com.example.data.models.user.User
import com.example.holders.ProfileDataEditAddItem.Companion.ACTION_ADD_FILE
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup

class ProfileDataAdditionalEditGroup(
        context: Context,
        notes: String?,
        files: List<RecommendationFile>,
        onExpandChange: OnExpandChange<ProfileExpandableSubtitleItem>,
        addFileClickListener: () -> Unit,
        private val onFileClick: (RecommendationFile) -> Unit,
        private val onFileEditClick: (RecommendationFile) -> Unit,
        private val saveClickListener: (data: Map<String, Any?>) -> Unit,
        private val cancelClickListener: () -> Unit
) : NestedGroup() {

    private val descriptionItem = ProfileDataNotesDescriptionItem(ID_DESCRIPTION)
    private val notesItem = ProfileDataNotesEditItem(ID_NOTES, notes)
    private val notesExpandableGroup = ProfileExpandableSubtitleGroup(context.getString(R.string.profile_notes), true, onExpandChange)
    private val fileExpandableGroup = ProfileExpandableSubtitleGroup(context.getString(R.string.profile_files), true, onExpandChange)
    private val addItem = ProfileDataEditAddItem(ID_ADD, ACTION_ADD_FILE, addFileClickListener)
    private val saveItem = ProfileDataEditSaveItem(ID_SAVE, {
        saveClickListener(mapOf(User.FIELD_USER_NOTES to notesItem.mNotes))
    }, {
        cancelClickListener()
    })

    private val fileItems = mutableListOf<ProfileDataFileEditableItem>()

    init {
        add(descriptionItem)
        add(notesExpandableGroup.apply { addAll(listOf(notesItem, saveItem)) })
        fileItems.addAll(files.map { createFileItem(it) })
        add(fileExpandableGroup.apply { addAll(fileItems) })
        add(addItem)
    }

    override fun getGroup(position: Int): Group {
        return when (position) {
            0 -> descriptionItem
            1 -> notesExpandableGroup
            2 -> fileExpandableGroup
            3 -> addItem
            else -> throw IndexOutOfBoundsException("Invalid item position: $position")
        }
    }

    override fun getPosition(group: Group): Int {
        return when (group) {
            descriptionItem -> 0
            notesExpandableGroup -> 1
            fileExpandableGroup -> 2
            addItem -> 3
            else -> -1
        }
    }

    override fun getGroupCount(): Int {
        return 4
    }

    private fun createFileItem(file: RecommendationFile): ProfileDataFileEditableItem {
        return ProfileDataFileEditableItem(
                ID_FILE + file.id,
                file,
                onFileClick,
                onFileEditClick,
                { item ->
                    fileItems.remove(item)
                    fileExpandableGroup.remove(item)
                    saveClickListener(mapOf(
                            User.FIELD_ATTACHED_FILES to fileItems.map { it.file }
                    ))
                }
        )
    }

    companion object {
        private const val ID_DESCRIPTION = 0L
        private const val ID_NOTES = 1L
        private const val ID_ADD = 2L
        private const val ID_SAVE = 3L
        private const val ID_FILE = 4L
    }
}