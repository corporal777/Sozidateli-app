package com.example.holders

import android.content.Context
import android.webkit.MimeTypeMap
import androidx.core.view.isVisible
import com.example.app.R
import com.example.data.models.FileModel
import com.example.app.databinding.ItemDocumentBinding
import com.example.extensions.formatToDefaultDate
import com.xwray.groupie.databinding.BindableItem
import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow

class DocumentItem(
    private val document: FileModel,
    private val onClick: () -> Unit
) : BindableItem<ItemDocumentBinding>(document.id?.toLong() ?: 0) {

    override fun bind(viewBinding: ItemDocumentBinding, position: Int) {
        viewBinding.apply {
            tvDate.apply {
                val date = document.createdDate?.formatToDefaultDate()
                text = date
                isVisible = date != null
            }
            tvDocumentName.apply {
                text = (document.name ?: document.name ?: "file")
            }

            tvFileData.apply {
                val extension = MimeTypeMap.getFileExtensionFromUrl(document.uri)
                    ?.let { if (it.isEmpty()) null else it }
                val fileSize = getSize(context, document.size ?: 0)
                text = listOfNotNull(extension, fileSize).joinToString("\n")
            }

            root.setOnClickListener { onClick() }
        }
    }


    private fun getSize(context: Context, sizeBytes: Long): String {
        val units = arrayOf(R.string.size_b, R.string.size_kb, R.string.size_mb, R.string.size_gb)
        val digitGroups = (log10(sizeBytes.toDouble()) / log10(1024.0)).toInt()
        val unit = if (digitGroups >= units.size) units.last() else units[digitGroups]
        val size = sizeBytes / 1024.0.pow(digitGroups.toDouble())
        return "${DecimalFormat("###0").format(size)} ${context.getString(unit)}"
    }

    override fun getLayout() = R.layout.item_document
}