package com.example.ui.support.newQuestion

import android.content.Context
import android.net.Uri
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.SupportFile
import com.example.data.models.SupportFileType
import com.example.databinding.ItemSupportFileBinding
import com.example.holders.redesign.EventPageItem
import com.example.util.FileUtils
import com.example.util.UriUtils
import com.example.util.UriUtils.getMimeType
import com.example.util.setImage
import com.xwray.groupie.databinding.BindableItem

class SupportFileItem(
    val file: SupportFile,
    val removeFileClick: (file : SupportFile) -> Unit
) : BindableItem<ItemSupportFileBinding>(file.hashCode().toLong()) {


    override fun bind(viewBinding: ItemSupportFileBinding, position: Int) {
        viewBinding.apply {
            ivImage.apply {
                isVisible = file.type == SupportFileType.IMAGE
                setImage(file.uri)
            }
            tvFileType.apply {
                isVisible = file.type == SupportFileType.FILE
                text = getFileMimeType(root.context)
            }
            ivRemove.setOnClickListener {
                removeFileClick.invoke(file)
            }
        }
    }

    private fun getFileMimeType(context: Context): String? {
        if (file.uri == null) return null

        val filePath = FileUtils.getPath(context, file.uri)
        val mimeType = FileUtils.getMimeType(context, file.uri)
        var typeText = if (filePath.isEmpty()) getMimeType(context, file.uri) else mimeType
        if (!typeText.isNullOrEmpty()) typeText = "." + typeText.toUpperCase()
        return typeText
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is SupportFileItem) return false
        if (file != other.file) return false
        return true
    }


    override fun getLayout(): Int = R.layout.item_support_file
}