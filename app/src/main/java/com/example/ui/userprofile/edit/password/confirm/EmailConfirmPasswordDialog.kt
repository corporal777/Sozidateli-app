package com.example.ui.userprofile.edit.password.confirm

import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.text.SpannableStringBuilder
import android.text.util.Linkify
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import com.example.R
import com.example.databinding.BottomSheetEmailMessageSentBinding
import com.example.ui.auth.confirm.email.ConfirmEmailCodePresenter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import performOnBackgroundOutOnMain
import removeUrlUnderline
import java.util.concurrent.TimeUnit


class EmailConfirmPasswordDialog(
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
    private var onSend:() -> Unit = {}

    private val compositeDisposable = CompositeDisposable()

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
            btnSend.apply {
                setButtonTextColor(R.color.text_color_repeat_code_button)
                setOnClickListener { onSend.invoke() }
            }

            btnClose.setOnClickListener {
                dismiss()
            }
            ivBack.setOnClickListener {
                dismiss()
            }
            setOnDismissListener {
                compositeDisposable.clear()
                onDismiss.invoke()
            }
        }
    }

    fun starTimer(){
        if (!this.isShowing) return
        compositeDisposable.clear()
        mBinding.btnSend.isEnabled = false

        compositeDisposable += Observable.interval(1000, TimeUnit.MILLISECONDS)
            .performOnBackgroundOutOnMain()
            .subscribeBy {
                val timeLeft = 60 - (it.toInt() + 1)
                mBinding.apply {
                    if (timeLeft > 0) btnSend.setButtonText("Отправить повторно · 0:$timeLeft")
                    else btnSend.setButtonText("Отправить повторно")
                }
                if (timeLeft <= 0) {
                    mBinding.btnSend.isEnabled = true
                    compositeDisposable.clear()
                }
            }
    }

    private fun showMessages(){
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_APP_EMAIL)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun setOnDismissCallback(block : () -> Unit) : EmailConfirmPasswordDialog {
        onDismiss = block
        return this
    }

    fun setOnSendAgainCallback(block : () -> Unit): EmailConfirmPasswordDialog{
        onSend = block
        return this
    }
}