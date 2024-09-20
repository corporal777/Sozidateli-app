package com.example.holders.registerEvent

import android.net.Uri
import android.util.Log
import com.example.data.models.EventFile
import com.example.data.models.EventRegisterFieldData
import com.example.holders.ProfileDataFileAddItem
import com.xwray.groupie.Group
import com.xwray.groupie.Item
import com.xwray.groupie.NestedGroup

class RegisterEventFileGroup(
    val fieldData: EventRegisterFieldData<EventFile?>,
    private val onDataChange: (fieldData: EventRegisterFieldData<*>) -> Unit,
    onAddClick: () -> Unit
) : NestedGroup() {

    private var fileItem: EventRegistrationFileItem? = null
    private val fileAddItem = ProfileDataFileAddItem(true, onAddClick, true)

    private val descriptions: MutableList<EventRegistrationDescriptionItem> = mutableListOf()

    init {
        checkFile()

        descriptions.add(EventRegistrationDescriptionItem(fieldData.field.name, null))
        if (fieldData.field.description != null) {
            descriptions.add(EventRegistrationDescriptionItem(null, fieldData.field.description))
        }

        if (!fieldData.field.values.isNullOrEmpty()) {
            val availableExtensions = EventRegistrationDescriptionItem(
                null,
                "Допустимые форматы: " + fieldData.field.values.joinToString()
            )
            descriptions.add(availableExtensions)
        }
    }

    fun checkFile() {
        val document = fieldData.value
        fileItem = if (document != null) {
            showError(false)
            createFileItem(document.name, document.path)
        } else null

        onDataChange(fieldData)
        notifyItemChanged(0)
    }

    fun showError(show: Boolean) {
        val item = descriptions.find { !it.title.isNullOrEmpty() }
        if (item != null) {
            item.isErrorShown = show
            notifyItemChanged(0)
        }
    }

    override fun getGroup(position: Int): Group {
        val descriptionsCount = descriptions.size
        return when {
            position in 0 until descriptionsCount -> descriptions[position]
            (position - descriptionsCount) == 0 -> fileItem ?: fileAddItem
            else -> throw IndexOutOfBoundsException("Max group count is ${groupCount}, but you want position $position")
        }
    }

    override fun getPosition(group: Group): Int {
        val descriptionIndex = descriptions.indexOf(group)
        if (descriptionIndex >= 0) return descriptionIndex

        val descriptionsCount = descriptions.size
        return when {
            fileItem != null && group == fileItem -> descriptionsCount
            group == fileAddItem -> descriptionsCount
            else -> -1
        }
    }

    override fun getGroupCount(): Int {
        return descriptions.size + 1
    }

    private fun createFileItem(fileName: String, path: Uri): EventRegistrationFileItem {
        return EventRegistrationFileItem(
            FILE_ITEM_ID,
            fileName,
            fieldData.field.required,
            path.scheme?.startsWith("http") != true,
            {
                fieldData.value = null
                this.fileItem = null
                notifyItemChanged(0)
                onDataChange(fieldData)
            },
            {
                fieldData.value?.name = it
            })
    }

    fun getId() = fieldData.field.id

    companion object {
        private const val FILE_ITEM_ID = 0L
        private const val FILE_ADD_ITEM_ID = 1L
    }
}