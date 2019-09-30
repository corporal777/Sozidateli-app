package com.example.holders

import android.text.Spanned
import android.text.style.UnderlineSpan
import androidx.core.text.toSpannable
import com.example.R
import com.example.data.models.user.RecommendationFile
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_profile_data_editable_file.*

class ProfileDataFileEditableItem(
        id: Long,
        val file: RecommendationFile,
        private val onFileClick: (RecommendationFile) -> Unit,
        private val onEditClick: (RecommendationFile) -> Unit,
        private val onRemoveClick: (ProfileDataFileEditableItem) -> Unit
) : Item(id) {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            val fileName = (if (file.desc.isNullOrBlank()) file.name else file.desc) ?: "file"
            tvFileName.text = fileName.toSpannable().apply {
                setSpan(UnderlineSpan(), 0, length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            }

            btnEdit.setOnClickListener { onEditClick(file) }
            btnDelete.setOnClickListener { onRemoveClick(this@ProfileDataFileEditableItem) }
            tvFileName.setOnClickListener { onFileClick(file) }
        }
    }

    override fun getLayout() = R.layout.item_profile_data_editable_file
}