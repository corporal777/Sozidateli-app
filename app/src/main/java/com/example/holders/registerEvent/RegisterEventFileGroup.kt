package com.example.holders.registerEvent

import android.net.Uri
import com.example.data.models.EventFile
import com.example.data.models.eventRegister.EventRegisterField
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup

class RegisterEventFileGroup(
    val fieldData: EventRegisterField<EventFile?>,
    private val onDataChange: (fieldData: EventRegisterField<*>) -> Unit,
    onAddClick: () -> Unit
) : NestedGroup() {

    private var fileItem: RegisterEventFileItem? = null
    private val fileAddItem =
        RegisterEventFileAddItem(onAddClick, fieldData.field.parameters?.extensions?.joinToString())
    private val descriptions =
        mutableListOf(RegisterEventFileDescItem(fieldData.field.name, fieldData.field.description))

    init {
        checkFile()
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

    private fun createFileItem(fileName: String, path: Uri): RegisterEventFileItem {
        return RegisterEventFileItem(
            FILE_ITEM_ID,
            fileName,
            fieldData.field.isRequired,
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