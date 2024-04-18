package com.example.holders

import com.example.R
import com.example.databinding.ItemActionButtonBinding
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_action_button_small.*


class ActionButtonItem(
    id: Long,
    private val action: Int,
    private val addClickListener: () -> Unit
) : BindableItem<ItemActionButtonBinding>(id) {

    var isEnabled = true
    private lateinit var mBinding: ItemActionButtonBinding

    override fun bind(viewBinding: ItemActionButtonBinding, position: Int) {
        mBinding = viewBinding
        viewBinding.btnAction.apply {

            val actionText: Int = when (action) {
                ACTION_ADD_RECORD -> R.string.add_record
                ACTION_ADD_FILE -> R.string.add_file
                ACTION_SHOW_ON_MAP -> R.string.event_contacts_watch_on_map
                ACTION_EVENT_REQUEST -> R.string.event_register_request
                ACTION_SEND -> R.string.send
                ACTION_SAVE -> R.string.save
                else -> R.string.add_record
            }
            setButtonText(resources.getString(actionText))
            isEnabled = this@ActionButtonItem.isEnabled
            setOnClickListener { addClickListener() }
        }
    }


    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (this === other) return true
        if (other !is ActionButtonItem) return false

        if (action != other.action) return false
        if (isEnabled != other.isEnabled) return false

        return true
    }

    fun showLoading(show : Boolean) {
        if (this::mBinding.isInitialized) mBinding.btnAction.showProgressLoading(show)
    }

    override fun getLayout() = R.layout.item_action_button

    companion object {
        const val ACTION_ADD_RECORD = 0
        const val ACTION_ADD_FILE = 1
        const val ACTION_SHOW_ON_MAP = 2
        const val ACTION_EVENT_REQUEST = 3
        const val ACTION_SEND = 4
        const val ACTION_SAVE = 5
    }
}