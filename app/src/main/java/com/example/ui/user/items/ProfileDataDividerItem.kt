package com.example.ui.user.items

import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import com.example.app.R
import com.example.app.databinding.ItemProfileDataDividerBinding
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder

class ProfileDataDividerItem () : BindableItem<ItemProfileDataDividerBinding>() {


    override fun bind(viewBinding: ItemProfileDataDividerBinding, position: Int) {
    }

    override fun getLayout() = R.layout.item_profile_data_divider
}