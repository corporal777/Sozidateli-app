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
        private val name: String,
        private val compactBottom: Boolean,
        private val onFileClick: () -> Unit
) : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            tvFileName.text = name.toSpannable().apply {
                setSpan(UnderlineSpan(), 0, length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            }
            divider.visibility = if (compactBottom) View.GONE else View.VISIBLE
            tvFileName.setOnClickListener { onFileClick() }
        }
    }

    override fun getLayout() = R.layout.item_profile_data_file
}