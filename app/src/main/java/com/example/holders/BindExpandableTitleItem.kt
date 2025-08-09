package com.example.holders

import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import com.example.app.R
import com.example.data.models.Notification
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.ExpandableItem
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.OnItemLongClickListener
import com.xwray.groupie.viewbinding.BindableItem


abstract class BindExpandableTitleItem<T : ViewDataBinding> (
    private val title: String
) : BindableItem<T>(title.hashCode().toLong()), ExpandableItem {

    var isExpanded: Boolean = false
    private lateinit var onToggleListener: ExpandableGroup

    @CallSuper
    override fun bind(viewBinding: T, position: Int) {
        viewBinding.apply {
            getTitleTextView(viewBinding).text = title
            setExpanded(this)
            root.setOnClickListener { onToggleListener.onToggleExpanded() }
        }
    }

    override fun bind(viewBinding: T, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNullOrEmpty()) super.bind(viewBinding, position, payloads)
        else viewBinding.apply {
            (payloads[0] as? Boolean)?.let {
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

    abstract fun setExpanded(viewBinding: T, isUpdate: Boolean = false)
    abstract fun getTitleTextView(viewBinding: T): TextView

    companion object {
        private const val EXPAND_CHANGE_ANIMATION_DURATION = 200
    }
}