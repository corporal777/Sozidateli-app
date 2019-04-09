package com.example.holders.profile

import android.text.InputType
import android.view.MotionEvent
import com.example.R
import com.example.data.models.ProfileField
import com.example.data.models.Type
import com.example.ui.profile.profileEdit.ProfileEditPresenter
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.field_profile.view.*

class ProfileFieldPasswordChangeItem(private val presenter: ProfileEditPresenter) : ProfileFieldItem(ProfileField(Type.PASSWORD, "_", "Пароль", false, "********")) {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)
        viewHolder.itemView.apply {
            editText.setOnTouchListener { view, motionEvent ->
                if (motionEvent.action == MotionEvent.ACTION_UP) {
                    presenter.onChangePasswordShowDialogClick()
                }
                return@setOnTouchListener true
            }
        }
    }

    override fun getInputType() = InputType.TYPE_TEXT_VARIATION_PASSWORD

    override fun getLayout() = R.layout.field_profile
}