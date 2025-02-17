package com.example.holders

import android.view.View
import com.example.app.R
import com.example.app.databinding.ItemProfileDataInterestBinding
import com.example.data.models.InterestNew
import com.xwray.groupie.viewbinding.BindableItem

class ProfileDataInterestItem(
    private val interest: InterestNew
) : BindableItem<ItemProfileDataInterestBinding>() {


    override fun bind(viewBinding: ItemProfileDataInterestBinding, position: Int) {
        viewBinding.apply {
            tvInterest.text = interest.name
        }
    }

    override fun initializeViewBinding(view: View) = ItemProfileDataInterestBinding.bind(view)
    override fun getLayout() = R.layout.item_profile_data_interest
}