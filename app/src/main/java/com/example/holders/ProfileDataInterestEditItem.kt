package com.example.holders

import android.view.View
import androidx.core.view.isVisible
import com.example.app.R
import com.example.app.databinding.ItemProfileDataEditInterestBinding
import com.example.data.models.UserInterest
import com.xwray.groupie.viewbinding.BindableItem
import com.xwray.groupie.viewbinding.GroupieViewHolder

class ProfileDataInterestEditItem(
    private val userInterest: UserInterest,
    private val compactBottom: Boolean,
    private val onCheckChanged: () -> Unit
) : BindableItem<ItemProfileDataEditInterestBinding>(userInterest.interest.id?.toLong() ?: 0) {

    override fun bind(viewBinding: ItemProfileDataEditInterestBinding, position: Int) {
        viewBinding.apply {
            cbInterest.apply {
                text = userInterest.interest.name
                isChecked = userInterest.isUserInterest
                setOnCheckedChangeListener { _, isChecked ->
                    userInterest.isUserInterest = isChecked
                    onCheckChanged()
                }
            }
            divider.isVisible = !compactBottom
        }
    }


    override fun unbind(viewHolder: GroupieViewHolder<ItemProfileDataEditInterestBinding>) {
        viewHolder.binding.cbInterest.apply {
            setOnCheckedChangeListener(null)
        }
        super.unbind(viewHolder)
    }


    override fun initializeViewBinding(view: View) = ItemProfileDataEditInterestBinding.bind(view)
    override fun getLayout() = R.layout.item_profile_data_edit_interest
}