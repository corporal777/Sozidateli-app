package com.example.holders

import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import com.example.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_profile_expandable_title.*

class ProfileExpandableTitleItem(
        title: String
) : ExpandableTitleItem(title) {

    var hideDividerOnExpand = true

    override fun setExpanded(viewHolder: GroupieViewHolder, isUpdate: Boolean) {
        viewHolder.tvTitle.apply {
            val icon = ContextCompat.getDrawable(context, if (isExpanded) R.drawable.ic_arrow_top else R.drawable.ic_arrow_bottom)
            setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null)
        }

        viewHolder.divider.apply {
            isInvisible = hideDividerOnExpand && isExpanded
        }
    }

    override fun getTitleTextView(viewHolder: GroupieViewHolder): TextView = viewHolder.tvTitle

    override fun getLayout() = R.layout.item_profile_expandable_title
}

typealias OnEditClickListener = () -> Unit