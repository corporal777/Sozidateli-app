package com.example.ui.support.items

import android.content.Context
import android.view.View
import androidx.fragment.app.FragmentManager
import com.example.app.R
import com.example.app.databinding.ItemSupportCenterFooterBinding
import com.example.ui.support.newQuestion.SupportQuestionBottomSheet
import com.xwray.groupie.viewbinding.BindableItem

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

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (other !is SupportFooterItem) return false
        return true
    }

    override fun initializeViewBinding(view: View) = ItemSupportCenterFooterBinding.bind(view)
    override fun getLayout(): Int = R.layout.item_support_center_footer
}