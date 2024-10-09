package com.example.holders

import com.example.app.R
import com.example.data.models.EventActivityModel
import com.example.app.databinding.ItemActionButtonBinding
import com.example.app.databinding.ItemLectureBinding
import com.xwray.groupie.databinding.BindableItem


class ActionButtonItem(
    id: Long,
    private val addClickListener: () -> Unit
) : BindableItem<ItemActionButtonBinding>(id) {

    var isEnabled = true

    override fun bind(viewBinding: ItemActionButtonBinding, position: Int) {
        viewBinding.btnAction.apply {
            isSelected = this@ActionButtonItem.isEnabled
            setOnClickListener { addClickListener() }
        }
    }

    override fun bind(viewBinding: ItemActionButtonBinding, position: Int, payloads: MutableList<Any>?) {
        val payload = payloads?.firstOrNull()
        if (payload == null) super.bind(viewBinding, position, payloads)
        else {
            if (payload is Int) {
                if (payload == 1) viewBinding.btnAction.showProgressLoading(true)
                else viewBinding.btnAction.showProgressLoading(false)
            }
        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (this === other) return true
        if (other !is ActionButtonItem) return false
        if (isEnabled != other.isEnabled) return false
        return true
    }

    override fun getLayout() = R.layout.item_action_button
}