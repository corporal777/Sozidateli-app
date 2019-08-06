package com.example.holders

import android.text.Spanned
import android.text.style.UnderlineSpan
import android.view.View
import androidx.core.text.toSpannable
import com.example.R
import com.example.data.models.user.RecommendationFile
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_profile_data_file.*

class ProfileDataFileItem(
        private val file: RecommendationFile,
        private val compactBottom: Boolean,
        private val onFileClick: (RecommendationFile) -> Unit
) : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            val fileName = (if (file.desc.isNullOrBlank()) file.name else file.desc) ?: "file"
            tvFileName.text = fileName.toSpannable().apply {
                setSpan(UnderlineSpan(), 0, length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            }
            divider.visibility = if (compactBottom) View.GONE else View.VISIBLE

            itemView.setOnClickListener { onFileClick(file) }
        }
    }

    override fun getLayout() = R.layout.item_profile_data_file
}