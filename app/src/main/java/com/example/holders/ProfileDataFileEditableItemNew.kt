package com.example.holders

import com.example.R
import com.example.data.models.FileModel
import com.example.databinding.ItemProfileDataEditableFileNewBinding
import com.example.util.initInput
import com.example.util.initSwitch
import com.xwray.groupie.databinding.BindableItem

class ProfileDataFileEditableItemNew(
    val file: FileModel,
    private val onFileClick: (FileModel) -> Unit,
    private val onRemoveClick: (ProfileDataFileEditableItemNew) -> Unit
) : BindableItem<ItemProfileDataEditableFileNewBinding>(file.size?.toLong() ?: 0) {

    override fun bind(viewBinding: ItemProfileDataEditableFileNewBinding, position: Int) {
        viewBinding.apply {
            tvFileName.apply {
                text = file.name
                isClickable = false
            }
            etFileName.apply {
                initInput(file.name) { file.name = it.toString() }
            }
            scFile.initSwitch(file.showInProfile ?: false) { file.showInProfile = it }
            btnDelete.setOnClickListener { onRemoveClick(this@ProfileDataFileEditableItemNew) }
            tvFileName.setOnClickListener { onFileClick(file) }
        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is ProfileDataFileEditableItemNew) return false
        if (file.id != other.file.id) return false
        if (file != other.file) return false
        return true
    }


    override fun getLayout() = R.layout.item_profile_data_editable_file_new

}