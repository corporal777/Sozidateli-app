package com.example.holders

import android.content.Context
import android.webkit.MimeTypeMap
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.Document
import com.example.data.models.FileModel
import com.example.extensions.formatToDefaultDate
import com.example.extensions.setUnderlineSpan
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_document.*
import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow

class DocumentItem(
        private val document: /*FileModel*/Document,
        private val onClick: () -> Unit
) : Item(document.id?.toLong()?: 0) {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            tvDate.apply {
                val date = document.public_date?.formatToDefaultDate()//document.createdDate?.formatToDefaultDate()
                text = date
                isVisible = date != null
            }
            tvDocumentName.apply {
                text = (document.description ?: document.filename ?: "file"/*document.name ?: document.name ?: "file"*/)
            }

            tvFileData.apply {
                val extension = MimeTypeMap.getFileExtensionFromUrl(/*document.uri*/document.file)?.let { if (it.isEmpty()) null else it }
                val fileSize = getSize(context, /*document.size*/document.fileSize ?: 0)
                text = listOfNotNull(extension, fileSize).joinToString("\n")
            }

            itemView.setOnClickListener { onClick() }
        }
    }

    private fun getSize(context: Context, sizeBytes: /*Long*/Int): String {
        val units = arrayOf(R.string.size_b, R.string.size_kb, R.string.size_mb, R.string.size_gb)
        val digitGroups = (log10(sizeBytes.toDouble()) / log10(1024.0)).toInt()
        val unit = if (digitGroups >= units.size) units.last() else units[digitGroups]
        val size = sizeBytes / 1024.0.pow(digitGroups.toDouble())
        return "${DecimalFormat("###0").format(size)} ${context.getString(unit)}"
    }

    override fun getLayout() = R.layout.item_document
}