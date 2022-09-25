package com.example.ui.user.items

import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import com.example.R
import com.example.databinding.ItemProfileDataDividerBinding
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_profile_expandable_title.*

class ProfileDataDividerItem () : BindableItem<ItemProfileDataDividerBinding>() {


    override fun bind(viewBinding: ItemProfileDataDividerBinding, position: Int) {
    }

    override fun getLayout() = R.layout.item_profile_data_divider
}