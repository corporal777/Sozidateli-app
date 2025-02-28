package com.example.holders.registerEvent

import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.example.app.R
import com.example.app.databinding.ItemProfileAttachFileBinding
import com.example.extensions.dp
import com.xwray.groupie.viewbinding.BindableItem

class RegisterEventFileAddItem(
    private val onClickListener: () -> Unit,
    private val extensions: String? = null
) : BindableItem<ItemProfileAttachFileBinding>(-1001L) {

    private var isEditable = true
    private var isLoadingVisible = false

    override fun bind(viewBinding: ItemProfileAttachFileBinding, position: Int) {
        viewBinding.apply {
            progressLoad.apply {
                isVisible = isLoadingVisible
                setProgressColor(ContextCompat.getColor(context, R.color.main_brown_color_new))
                setSize(20.dp)
            }
            tvEdit.apply {
                isInvisible = isLoadingVisible
                isEnabled = isEditable
                setOnClickListener {
                    onClickListener.invoke()
                }
                updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    this.topMargin = 10.dp
                }
            }
            tvHelp.text =
                if (extensions.isNullOrEmpty())
                    root.context.getString(R.string.user_profile_upload_photo_help_short)
                else "${extensions.uppercase()}; не более 10 МБ"
        }
    }


    fun updateButton(payload: Boolean) {
        isEditable = payload
    }

    fun showLoading() {
        isLoadingVisible = true
    }

    fun hideLoading() {
        isLoadingVisible = false
    }

    override fun initializeViewBinding(view: View) = ItemProfileAttachFileBinding.bind(view)
    override fun getLayout() = R.layout.item_profile_attach_file
}