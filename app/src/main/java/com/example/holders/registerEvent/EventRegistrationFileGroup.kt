package com.example.holders.registerEvent

import android.net.Uri
import com.example.data.models.EventFile
import com.example.data.models.RegisterEventFieldData
import com.example.holders.ActionButtonItem
import com.example.holders.ActionButtonItem.Companion.ACTION_ADD_FILE
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup

class EventRegistrationFileGroup(
        val fieldData: RegisterEventFieldData<EventFile?>,
        private val onDataChange: (fieldData: RegisterEventFieldData<*>) -> Unit,
        onAddClick: () -> Unit
) : NestedGroup() {

    private var fileItem: EventRegistrationFileItem? = null
    private val fileAddItem = ActionButtonItem(FILE_ADD_ITEM_ID, ACTION_ADD_FILE, onAddClick)

    init {
        checkFile()
    }

    fun checkFile() {
        val document = fieldData.value
        fileItem = if (document != null) {
            createFileItem(document.name, document.path)
        } else {
            null
        }
        onDataChange(fieldData)
        notifyItemChanged(0)
    }

    override fun getGroup(position: Int): Group {
        return if (position == 0) fileItem ?: fileAddItem
        else throw IndexOutOfBoundsException("Max group count is ${groupCount}, but you want position $position")
    }

    override fun getPosition(group: Group): Int {
        return when {
            fileItem != null && group == fileItem -> 0
            group == fileAddItem -> 0
            else -> -1
        }
    }

    override fun getGroupCount() = 1

    private fun createFileItem(fileName: String, path: Uri): EventRegistrationFileItem {
        return EventRegistrationFileItem(FILE_ITEM_ID, fileName, path.scheme?.startsWith("http") != true, {
            fieldData.value = null
            this.fileItem = null
            notifyItemChanged(0)
            onDataChange(fieldData)
        }, {
            fieldData.value?.name = it
        })
    }

    companion object {
        private const val FILE_ITEM_ID = 0L
        private const val FILE_ADD_ITEM_ID = 1L
    }
}