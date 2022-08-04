package com.example.ui.accountChange.items

import android.util.Log
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.EventActivityModel
import com.example.data.models.UserDetail
import com.example.data.models.UserSessionModel
import com.example.databinding.ItemAccountChangeBinding
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_lecture.*
import setCircleImage
import setOnClickListener

class AccountItem(
    var canShow: Boolean,
    val session: UserSessionModel,
    val currentAccountId: String,
    val user: UserDetail,
    val onMenuClick: (user: UserDetail) -> Unit,
    val onAccountClick: (user: UserDetail) -> Unit
) : BindableItem<ItemAccountChangeBinding>(session.sessionId.toLong()) {

    val isCurrentUser = currentAccountId == user.id.toString()
    override fun bind(viewBinding: ItemAccountChangeBinding, position: Int) {
        viewBinding.apply {
            ivAvatar.setCircleImage(user.image.uri, R.drawable.avatar_placeholder)
            tvName.text = user.nameLastName
            if (user.email != null && !user.email?.value.isNullOrEmpty()) {
                tvEmail.text = user.email?.value
            } else {
                tvEmail.text = user.phone?.get(0)?.value
            }

            if (isCurrentUser && session.isLogged) {
                if (session.isLogged){
                    cardAccountStatus.isVisible = true
                }
            }

            decorMenuButton(canShow, ivMenu)
            ivMenu.setOnClickListener {
                onMenuClick(user)
            }
            clAccount.setOnClickListener {
                onAccountClick.invoke(user)
            }
        }
    }


    override fun bind(
        viewBinding: ItemAccountChangeBinding,
        position: Int,
        payloads: MutableList<Any>?
    ) {
        val payload = payloads?.firstOrNull()
        if (payload == null) super.bind(viewBinding, position, payloads)
        else {
            if (payload is Boolean) {
                decorMenuButton(payload, viewBinding.ivMenu)
            }
        }
    }

    private fun decorMenuButton(canShow : Boolean, imageView: ImageView){
        imageView.isVisible = canShow
        Log.e("STATE", canShow.toString())
    }

    override fun getLayout(): Int = R.layout.item_account_change
}