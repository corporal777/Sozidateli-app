package com.example.holders

import com.example.R
import com.example.data.models.FileModel
import com.example.data.models.user.RecommendationFile
import com.example.util.initInput
import com.example.util.initSwitch
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_profile_data_editable_file_new.*

class ProfileDataFileEditableItemNew(
        id: Long,
        val file: FileModel,
        private val onFileClick: (FileModel) -> Unit,
        private val onEditClick: (FileModel) -> Unit,
        private val onRemoveClick: (ProfileDataFileEditableItemNew) -> Unit
) : Item(id) {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            //val fileName = (if (file.name.isNullOrBlank()) file.name else file.desc) ?: "file"
            val fileName = file.name
            tvFileName.apply {
                text = fileName
                isClickable = false
            }
            etFileName.apply {
                //initInput(file.newName) { file.newName = it.toString() }
                initInput(file.name) { file.name = it.toString() }
            }
            scFile.initSwitch(file.showInProfile?: false) { file.showInProfile = it }
            btnEdit.setOnClickListener { onEditClick(file) }
            btnDelete.setOnClickListener { onRemoveClick(this@ProfileDataFileEditableItemNew) }
            tvFileName.setOnClickListener { onFileClick(file) }
        }
    }

    override fun getLayout() = R.layout.item_profile_data_editable_file_new
}