package com.example.holders

import com.example.R
import com.example.data.models.user.RecommendationFile
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_profile_data_editable_file.*

class ProfileDataFileEditableItem(
        id: Long,
        val file: RecommendationFile,
        private val onFileClick: (RecommendationFile) -> Unit,
        private val onEditClick: (RecommendationFile) -> Unit,
        private val onRemoveClick: (ProfileDataFileEditableItem) -> Unit
) : Item(id) {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            val fileName = (if (file.desc.isNullOrBlank()) file.name else file.desc) ?: "file"
            tvFileName.apply {
                text = fileName
                isClickable = false
            }

            btnEdit.setOnClickListener { onEditClick(file) }
            btnDelete.setOnClickListener { onRemoveClick(this@ProfileDataFileEditableItem) }
            tvFileName.setOnClickListener { onFileClick(file) }
        }
    }

    override fun getLayout() = R.layout.item_profile_data_editable_file
}