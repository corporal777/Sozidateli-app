package com.example.ui.accountChange.items

import android.widget.ImageView
import androidx.core.view.isVisible
import com.example.app.R
import com.example.data.models.UserSessionModel
import com.example.app.databinding.ItemAccountChangeBinding
import com.example.util.setCircleAvatar
import com.xwray.groupie.databinding.BindableItem

class AccountItem(
    var canShow: Boolean,
    val session: UserSessionModel,
    val currentAccountId: String,
    val onMenuClick: (session: UserSessionModel) -> Unit,
    val onAccountClick: (session: UserSessionModel) -> Unit
) : BindableItem<ItemAccountChangeBinding>(session.sessionId.toLong()) {

    private val isCurrentUser = currentAccountId == session.binds.user.id.toString()
    private val userLogin =
        if (session.binds.user.email != null && !session.binds.user.email?.value.isNullOrEmpty()) {
            session.binds.user.email?.value
        } else session.binds.user.phone?.get(0)?.value

    override fun bind(viewBinding: ItemAccountChangeBinding, position: Int) {
        viewBinding.apply {
            ivAvatar.setCircleAvatar(session.binds.user.loadUserImage())
            tvName.text = session.binds.user.nameLastName
            tvEmail.text = userLogin

            if (isCurrentUser && session.isLogged) cardAccountStatus.isVisible = true

            decorMenuButton(canShow, ivMenu)

            ivMenu.setOnClickListener {
                onMenuClick(session)
            }
            clAccount.setOnClickListener {
                onAccountClick.invoke(session)
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
            if (payload is Boolean) decorMenuButton(payload, viewBinding.ivMenu)
        }
    }

    private fun decorMenuButton(canShow: Boolean, imageView: ImageView) {
        imageView.isVisible = canShow
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is AccountItem) return false
        if (session != other.session) return false
        return true
    }

    override fun getLayout(): Int = R.layout.item_account_change
}