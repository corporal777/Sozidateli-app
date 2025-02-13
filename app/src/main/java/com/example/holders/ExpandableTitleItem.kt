package com.example.holders

import android.widget.TextView
import androidx.databinding.ViewDataBinding
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.ExpandableItem
import com.xwray.groupie.viewbinding.BindableItem


abstract class ExpandableTitleItem<T : ViewDataBinding>(
        private val title: String
) : BindableItem<T>(title.hashCode().toLong()), ExpandableItem {

    var isExpanded: Boolean = false
    private lateinit var onToggleListener: ExpandableGroup

    override fun bind(viewBinding: T, position: Int) {
        viewBinding.apply {
            getTitleTextView(viewBinding).text = title
            setExpanded(this)
            root.setOnClickListener { onToggleListener.onToggleExpanded() }
        }
    }

    override fun bind(viewBinding: T, position: Int, payloads: MutableList<Any>) {
        if (payloads?.isEmpty() == true) super.bind(viewBinding, position, payloads)
        else viewBinding.apply {
            (payloads?.get(0) as? Boolean)?.let {
                isExpanded = it
                setExpanded(viewBinding, true)
            }
        }
    }

    override fun setExpandableGroup(onToggleListener: ExpandableGroup) {
        this.isExpanded = onToggleListener.isExpanded
        this.onToggleListener = onToggleListener
        registerGroupDataObserver(onToggleListener)
    }

    abstract fun setExpanded(binding: T, isUpdate: Boolean = false)
    abstract fun getTitleTextView(binding: T): TextView

    companion object {
        private const val EXPAND_CHANGE_ANIMATION_DURATION = 200
    }
}