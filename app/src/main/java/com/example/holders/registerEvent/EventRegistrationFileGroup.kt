package com.example.holders.registerEvent

import com.example.data.models.Document
import com.example.data.models.RegisterEventFieldData
import com.example.holders.ActionButtonItem
import com.example.holders.ActionButtonItem.Companion.ACTION_ADD_FILE
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup

class EventRegistrationFileGroup(
        val fieldData: RegisterEventFieldData<Document>,
        onAddClick: () -> Unit
) : NestedGroup() {

    private var fileItem: EventRegistrationFileItem? = null
    private val fileAddItem = ActionButtonItem(FILE_ADD_ITEM_ID, ACTION_ADD_FILE, onAddClick)

    private var fileName: String? = null
    private var filePath: String? = null

    init {
        val document = fieldData.value
        if (document != null) {
            fileName = document.filename
            filePath = document.file
            fileItem = createFileItem(document.filename)
        }
    }

    fun addFile(name: String, path: String) {
        fileName = name
        filePath = path
        fileItem = createFileItem(name)
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

    private fun createFileItem(fileName: String): EventRegistrationFileItem {
        return EventRegistrationFileItem(FILE_ITEM_ID, fileName, {
            this.fileName = null
            this.filePath = null
            this.fileItem = null
            notifyItemChanged(0)
        }) { this.fileName = it }
    }

    companion object {
        private const val FILE_ITEM_ID = 0L
        private const val FILE_ADD_ITEM_ID = 1L
    }
}