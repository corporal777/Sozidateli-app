package com.example.ui.userprofile.edit.password.confirm

import android.content.Context
import android.content.Intent
import android.text.SpannableStringBuilder
import android.text.util.Linkify
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import com.example.R
import com.example.databinding.BottomSheetEmailMessageSentBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import removeUrlUnderline


class EmailConfirmPasswordFragment(
    private val context: Context,
    private val email: String,
) : BottomSheetDialog(context) {

    private val mBinding = BottomSheetEmailMessageSentBinding.inflate(LayoutInflater.from(context))
    private val message = SpannableStringBuilder(
        context.getString(R.string.recovery_confirm_email_message).format(email)
    ).apply {
        append(" ")
        append(context.getString(R.string.support_email))
        append(".")
    }
    private var onDismiss:() -> Unit = {}

    init {
        setContentView(mBinding.root)
        setCancelable(false)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.skipCollapsed = true

        mBinding.apply {
            tvDescription.apply {
                Linkify.addLinks(message, Linkify.EMAIL_ADDRESSES)
                highlightColor = ContextCompat.getColor(context, R.color.profile_id_text)
                text = message
                movementMethod = BetterLinkMovementMethod.getInstance()
                removeUrlUnderline()
            }

            btnCheck.setOnClickListener {
                showMessages()
                dismiss()
            }

            btnClose.setOnClickListener {
                dismiss()
            }
            ivBack.setOnClickListener {
                dismiss()
            }
            setOnDismissListener {
                onDismiss.invoke()
            }
        }
    }

    private fun showMessages(){
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_APP_EMAIL)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun setOnDismissCallback(block : () -> Unit) : EmailConfirmPasswordFragment {
        onDismiss = block
        return this
    }
}