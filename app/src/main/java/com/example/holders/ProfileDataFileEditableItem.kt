package com.example.holders

import android.text.TextWatcher
import android.view.View
import com.example.app.R
import com.example.data.models.FileModel
import com.example.app.databinding.ItemProfileDataEditableFileBinding
import com.xwray.groupie.Item
import com.example.common.extensions.onTextChanged
import com.xwray.groupie.viewbinding.BindableItem
import com.xwray.groupie.viewbinding.GroupieViewHolder


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
    override fun bind(viewBinding: ItemProfileDataEditableFileBinding, position: Int) {
        viewBinding.apply {
            tvShowFile.setOnClickListener {
                onFileClick(file)
            }
            etFile.apply {
                setIconClickCallback { onRemoveClick(file) }
                getEditText().apply {
                    textWatcher?.let { removeTextChangedListener(it) }
                    setText(fileName)
                    textWatcher = onTextChanged(textChangeListener)
                }
            }
            scFile.apply {
                setChecked(file.showInProfile ?: false)
                setOnCheckedListener {
                    file.showInProfile = it
                }
            }
        }
    }

    override fun unbind(viewHolder: GroupieViewHolder<ItemProfileDataEditableFileBinding>) {
        viewHolder.binding.apply {
            etFile.getEditText().apply {
                textWatcher?.let { removeTextChangedListener(it) }
            }
        }
        super.unbind(viewHolder)
    }

    override fun hasSameContentAs(other: Item<*>): Boolean {
        if (other !is ProfileDataFileEditableItem) return false
        if (file != other.file) return false
        return true
    }

    fun getFileName() = fileName


    override fun initializeViewBinding(view: View) = ItemProfileDataEditableFileBinding.bind(view)
    override fun getLayout() = R.layout.item_profile_data_editable_file
}