package com.example.holders

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.example.R
import com.example.databinding.ItemProfileAttachFileBinding
import com.example.databinding.ItemProfileButtonEditBinding
import com.example.extensions.dp
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_profile_button_edit.*

class ProfileDataFileAddItem(
    private val isButtonEditable : Boolean,
    private val onClickListener: () -> Unit
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
            }
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