package com.example.holders

import com.example.R
import com.example.data.models.user.RecommendationFile
import com.example.util.initInput
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_profile_data_editable_file_new.*

class ProfileDataFileEditableItemNew(
        id: Long,
        val file: RecommendationFile,
        private val onFileClick: (RecommendationFile) -> Unit,
        private val onEditClick: (RecommendationFile) -> Unit,
        private val onRemoveClick: (ProfileDataFileEditableItemNew) -> Unit
) : Item(id) {

    var newName = ""

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            val fileName = (if (file.desc.isNullOrBlank()) file.name else file.desc) ?: "file"
            newName = fileName
            tvFileName.apply {
                text = fileName
                isClickable = false
            }
            etFileName.apply {
                initInput(fileName) { newName = it.toString() }
            }
            btnEdit.setOnClickListener { onEditClick(file) }
            btnDelete.setOnClickListener { onRemoveClick(this@ProfileDataFileEditableItemNew) }
            tvFileName.setOnClickListener { onFileClick(file) }
        }
    }

    override fun getLayout() = R.layout.item_profile_data_editable_file_new
}