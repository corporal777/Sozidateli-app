package com.example.holders

import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.example.app.R
import com.example.app.databinding.ItemProfileAttachFileBinding
import com.example.extensions.dp
import com.xwray.groupie.databinding.BindableItem

class ProfileDataFileAddItem(
    private val isButtonEditable : Boolean,
    private val onClickListener: () -> Unit,
    private val isShort : Boolean = false
) : BindableItem<ItemProfileAttachFileBinding>(-1001L) {

    private var isEditable = isButtonEditable
    private var isLoadingVisible = false

    private lateinit var mBinding : ItemProfileAttachFileBinding
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
                    this.topMargin = if (isShort) 10.dp else 15.dp
                }
            }
            tvHelp.text = if (!isShort) root.context.getString(R.string.user_profile_upload_photo_help)
            else root.context.getString(R.string.user_profile_upload_photo_help_short)
        }
    }


    fun updateButton(payload: Boolean){
        isEditable = payload
    }

    fun showLoading(){
        isLoadingVisible = true
    }

    fun hideLoading(){
        isLoadingVisible = false
    }


    override fun getLayout() = R.layout.item_profile_attach_file
}