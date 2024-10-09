package com.example.ui.support.items

import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.app.R
import com.example.app.databinding.ItemSupportExpandableQuestionTitleBinding
import com.example.holders.BindExpandableTitleItem
import com.example.holders.ExpandableTitleItem
import com.example.holders.redesign.EventPageItem
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder

class SupportQuestionExpandableTitleItem(
    title: String
) : BindExpandableTitleItem<ItemSupportExpandableQuestionTitleBinding>(title) {

    override fun setExpanded(
        viewBinding: ItemSupportExpandableQuestionTitleBinding,
        isUpdate: Boolean
    ) {
        viewBinding.apply {
            if (isExpanded) ivArrow.setImageResource(R.drawable.ic_arrow_top)
            else ivArrow.setImageResource(R.drawable.ic_arrow_bottom)
        }
    }

    override fun getTitleTextView(viewBinding: ItemSupportExpandableQuestionTitleBinding): TextView {
        return viewBinding.tvTitle
    }

    override fun getLayout() = R.layout.item_support_expandable_question_title
}