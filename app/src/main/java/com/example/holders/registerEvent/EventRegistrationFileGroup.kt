package com.example.holders.registerEvent

import android.content.Context
import android.net.Uri
import com.example.R
import com.example.data.models.EventFile
import com.example.data.models.EventRegisterFieldData
import com.example.holders.ProfileButtonEditItem
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.kotlinandroidextensions.Item

class EventRegistrationFileGroup(
        context: Context,
        val fieldData: EventRegisterFieldData<EventFile?>,
        private val onDataChange: (fieldData: EventRegisterFieldData<*>) -> Unit,
        onAddClick: () -> Unit
) : NestedGroup() {

    private var fileItem: EventRegistrationFileItem? = null
    private val fileAddItem = ProfileButtonEditItem(context.getString(R.string.add_file), onAddClick).apply {
        hasDivider = false
    }

    private val availableExtensions: Item?

    init {
        checkFile()

        availableExtensions = if (!fieldData.field.values.isNullOrEmpty()) {
            EventRegistrationAvailableExtensionsItem(String.format(
                    context.getString(R.string.event_register_available_extensions),
                    fieldData.field.values.joinToString()
            ))
        } else {
            null
        }
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
        return when {
            availableExtensions != null && position == 0 -> availableExtensions
            availableExtensions != null && position == 1 -> fileItem ?: fileAddItem
            position == 0 -> fileItem ?: fileAddItem
            else -> {
                throw IndexOutOfBoundsException("Max group count is ${groupCount}, but you want position $position")
            }
        }
    }

    override fun getPosition(group: Group): Int {
        val hasAvailableExtensions = availableExtensions != null
        return when {
            hasAvailableExtensions && group == availableExtensions -> 0
            fileItem != null && group == fileItem -> if (hasAvailableExtensions) 1 else 0
            group == fileAddItem -> if (hasAvailableExtensions) 1 else 0
            else -> -1
        }
    }

    override fun getGroupCount(): Int {
        return if (availableExtensions != null) 2
        else 1
    }

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