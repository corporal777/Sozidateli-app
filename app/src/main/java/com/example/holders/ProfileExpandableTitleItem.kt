package com.example.holders

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.app.R
import com.example.app.databinding.ItemProfileExpandableTitleBinding

class ProfileExpandableTitleItem(
        title: String
) : ExpandableTitleItem<ItemProfileExpandableTitleBinding>(title) {


    override fun setExpanded(binding: ItemProfileExpandableTitleBinding, isUpdate: Boolean) {
        binding.tvTitle.apply {
            val icon = ContextCompat.getDrawable(context, if (isExpanded) R.drawable.ic_arrow_top else R.drawable.ic_arrow_bottom)
            setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null)
        }
    }

    override fun getTitleTextView(binding: ItemProfileExpandableTitleBinding): TextView = binding.tvTitle

    override fun initializeViewBinding(view: View) = ItemProfileExpandableTitleBinding.bind(view)
    override fun getLayout() = R.layout.item_profile_expandable_title
}