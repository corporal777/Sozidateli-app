package com.example.ui.event.registration.items

import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.TextView
import com.example.R
import com.example.data.models.FileModel
import com.example.databinding.ItemRegisterEventProfileFileBinding
import com.example.extensions.parseAsHtmlWithoutUnderline
import com.example.holders.registerEvent.BaseRegisterProfileItem
import com.example.util.getColor
import com.example.util.showCustomTabsBrowser
import com.example.util.showFileBrowser
import com.xwray.groupie.databinding.BindableItem
import me.saket.bettermovementmethod.BetterLinkMovementMethod

class REProfileFileItem (
    val name: String,
    val files: List<FileModel>?
) : BaseRegisterProfileItem<ItemRegisterEventProfileFileBinding>(name.hashCode().toString().toLong()) {


    override fun bind(viewBinding: ItemRegisterEventProfileFileBinding, position: Int) {
        super.bind(viewBinding, position)
        viewBinding.tvFileName.apply {
            if (isErrorShown) setTextColor(getColor(R.color.red_new))
            else setTextColor(getColor(R.color.about_event_date))


            highlightColor = getColor(R.color.event_tabs_text_unchecked)
            movementMethod = LinkMovementMethod.getInstance()

            if (files.isNullOrEmpty()) text = context.getString(R.string.user_profile_files_absent)
            else {
                var filesText = ""
                files.forEachIndexed { index, file ->
                    filesText +=
                        if (index == 0) "<a href='${file.uri}'>${file.name}</a>"
                        else "<br><br><a href='${file.uri}'>${file.name}</a>"
                }
                text = filesText.parseAsHtmlWithoutUnderline()
                BetterLinkMovementMethod.linkifyHtml(this)
                    .setOnLinkClickListener { _, url ->
                        val type = files.find { x -> x.uri == url }
                        if (type?.isFilePDF() == true) showFileBrowser(context, url)
                        else showCustomTabsBrowser(context, url)
                        true
                    }
            }


        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is REProfileFileItem) return false
        if (name != other.name) return false
        if (files != other.files) return false
        return true
    }

    override fun getTitleView(binding: ItemRegisterEventProfileFileBinding): TextView? = null
    override fun getErrorFrameView(binding: ItemRegisterEventProfileFileBinding): View? = null
    override fun getLayout(): Int = R.layout.item_register_event_profile_file
}