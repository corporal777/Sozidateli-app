package com.example.ui.support.items

import android.view.View
import android.widget.TextView
import com.example.app.R
import com.example.app.databinding.ItemSupportExpandableQuestionTitleBinding
import com.example.holders.BindExpandableTitleItem

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
    override fun initializeViewBinding(view: View) = ItemSupportExpandableQuestionTitleBinding.bind(view)
}