package com.example.holders

import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.example.R
import com.example.data.models.FileModel
import com.example.databinding.ItemProfileDataEditableFileBinding
import com.example.extensions.dp
import com.example.util.initInput
import com.example.util.initSwitch
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.databinding.GroupieViewHolder
import kotlinx.android.synthetic.main.item_register_event_input.*
import onTextChanged


class ProfileDataFileEditableItem(
    val file: FileModel,
    private val onFileClick: (file: FileModel) -> Unit,
    private val onRemoveClick: (file: FileModel) -> Unit
) : BindableItem<ItemProfileDataEditableFileBinding>(file.id?.toLong()?:0){

    private var fileName = file.name
    private val textChangeListener: (CharSequence?) -> Unit = {
        fileName = it?.toString()
    }
    private var textWatcher: TextWatcher? = null


    private lateinit var mBinding: ItemProfileDataEditableFileBinding
    override fun bind(viewBinding: ItemProfileDataEditableFileBinding, position: Int) {
        mBinding = viewBinding
        viewBinding.apply {
            tilFile.apply {
                setEndIconOnClickListener {
                    showLoadingIcon()
                    onRemoveClick(file)
                }
            }
            etFile.apply {
                setText(fileName)
                textWatcher = onTextChanged(textChangeListener)
            }

            tvShowFile.apply {
                setOnClickListener {
                    onFileClick(file)
                }
            }

            scFile.initSwitch(file.showInProfile ?: false) {
                file.showInProfile = it
            }
        }
    }

    override fun unbind(viewHolder: GroupieViewHolder<ItemProfileDataEditableFileBinding>) {
        viewHolder.apply {
            viewHolder.binding.apply {
                etFile.apply {
                    textWatcher?.let { removeTextChangedListener(it) }
                }
            }
        }
        super.unbind(viewHolder)
    }

    override fun hasSameContentAs(other: Item<*>?): Boolean {
        if (other !is ProfileDataFileEditableItem) return false
        if (file != other.file) return false
        return true
    }

    fun getFileName() = fileName

    fun hideLoadingIcon(){
        if (this::mBinding.isInitialized) mBinding.tilFile.hideLoadingIcon()
    }


    override fun getLayout() = R.layout.item_profile_data_editable_file
}