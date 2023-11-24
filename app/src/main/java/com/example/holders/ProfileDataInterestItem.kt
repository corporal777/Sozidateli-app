package com.example.holders

import com.example.R
import com.example.data.models.InterestNew
import com.example.databinding.ItemProfileDataInterestBinding
import com.xwray.groupie.databinding.BindableItem

class ProfileDataInterestItem(
    private val interest: InterestNew
) : BindableItem<ItemProfileDataInterestBinding>() {


    override fun bind(viewBinding: ItemProfileDataInterestBinding, position: Int) {
        viewBinding.apply {
            tvInterest.text = interest.name
        }
    }

    override fun getLayout() = R.layout.item_profile_data_interest
}