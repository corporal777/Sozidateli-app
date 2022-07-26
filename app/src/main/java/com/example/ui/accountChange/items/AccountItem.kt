package com.example.ui.accountChange.items

import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.UserDetail
import com.example.databinding.ItemAccountChangeBinding
import com.xwray.groupie.databinding.BindableItem
import setCircleImage
import setOnClickListener

class AccountItem(
    val currentAccountId: String,
    val user: UserDetail,
    val onMenuClick: (user : UserDetail) -> Unit,
    val onAccountClick: (user : UserDetail) -> Unit
) : BindableItem<ItemAccountChangeBinding>() {

    val isCurrentUser = currentAccountId == user.id.toString()
    override fun bind(viewBinding: ItemAccountChangeBinding, position: Int) {
        viewBinding.apply {
            ivAvatar.setCircleImage(user.image.uri, R.drawable.avatar_placeholder)
            tvName.text = user.nameLastName
            tvEmail.text = user.email?.value
            cardAccountStatus.isVisible = isCurrentUser
            ivMenu.setOnClickListener {
                onMenuClick(user)
            }
            clAccount.setOnClickListener {
                onAccountClick.invoke(user)
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_account_change
}