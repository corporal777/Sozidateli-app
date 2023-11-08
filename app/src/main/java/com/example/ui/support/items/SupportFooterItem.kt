package com.example.ui.support.items

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.example.R
import com.example.databinding.ItemSupportCenterFooterBinding
import com.example.holders.redesign.EventActivityItem
import com.example.ui.support.newQuestion.SupportQuestionBottomSheet
import com.xwray.groupie.databinding.BindableItem

class SupportFooterItem(
    val context: Context,
    val manager: FragmentManager
) : BindableItem<ItemSupportCenterFooterBinding>(-1002L) {
    override fun bind(viewBinding: ItemSupportCenterFooterBinding, position: Int) {
        viewBinding.btnSendQuestion.setOnClickListener {
            showWriteQuestion()
        }
    }

    private fun showWriteQuestion() {
        SupportQuestionBottomSheet(context, manager)
            .setSendClickCallback {}
            .show()
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is SupportFooterItem) return false
        return true
    }

    override fun getLayout(): Int = R.layout.item_support_center_footer
}