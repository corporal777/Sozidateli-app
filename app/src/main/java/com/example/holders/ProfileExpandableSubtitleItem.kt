package com.example.holders

import android.widget.TextView
import androidx.core.view.isInvisible
import com.example.app.R
import com.example.app.databinding.ItemProfileExpandableSubtitleBinding

class ProfileExpandableSubtitleItem(
        title: String
) : ExpandableTitleItem<ItemProfileExpandableSubtitleBinding>(title) {

    var badgeCount = 0

    override fun bind(viewBinding: ItemProfileExpandableSubtitleBinding, position: Int) {
        super.bind(viewBinding, position)
        setBadge(viewBinding, badgeCount)
    }

    override fun bind(
        viewBinding: ItemProfileExpandableSubtitleBinding,
        position: Int,
        payloads: MutableList<Any>?
    ) {
        val payload = payloads?.firstOrNull()
        if (payload as? Int != null) setBadge(viewBinding, payload)
        else super.bind(viewBinding, position, payloads)
    }

    private fun setBadge(viewBinding: ItemProfileExpandableSubtitleBinding, count: Int) {
        badgeCount = count
        viewBinding.tvBadge.apply {
            isInvisible = count <= 0
            text = count.toString()
        }
    }

    override fun setExpanded(binding: ItemProfileExpandableSubtitleBinding, isUpdate: Boolean) {
        binding.ivArrow.apply {
            val toRotation = if (isExpanded) 0f else 180f
            if (isUpdate) {
                animate().rotation(toRotation).duration = EXPAND_CHANGE_ANIMATION_DURATION.toLong()
            } else {
                rotation = toRotation
            }
        }
    }

    override fun getTitleTextView(binding: ItemProfileExpandableSubtitleBinding): TextView = binding.tvTitle

    override fun getLayout() = R.layout.item_profile_expandable_subtitle

    companion object {
        private const val EXPAND_CHANGE_ANIMATION_DURATION = 200
    }
}