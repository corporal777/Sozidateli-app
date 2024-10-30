package com.example.ui.userprofile.common.password.confirm

import android.content.Context
import android.content.Intent
import android.content.pm.LabeledIntent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.text.SpannableStringBuilder
import android.text.util.Linkify
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import com.example.app.R
import com.example.app.databinding.BottomSheetEmailMessageSentBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import performOnBackgroundOutOnMain
import com.example.extensions.removeUrlUnderline
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
        try {
            val emailIntent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:"))
            val resInfo = context.packageManager.queryIntentActivities(emailIntent, 0)
            if (resInfo.isNotEmpty()) {
                val intentChooser = context.packageManager.getLaunchIntentForPackage(
                    resInfo.first().activityInfo.packageName
                )
                val openInChooser = Intent.createChooser(intentChooser, "Open E-mail")
                val packageManager = context.packageManager
                val emailApps = resInfo.toLabeledIntentArray(packageManager)
                openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, emailApps)
                openInChooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(openInChooser, null)
            } else {
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun List<ResolveInfo>.toLabeledIntentArray(packageManager: PackageManager): Array<LabeledIntent> =
        map {
            val packageName = it.activityInfo.packageName
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            LabeledIntent(intent, packageName, it.loadLabel(packageManager), it.icon)
        }.toTypedArray()

    fun setOnDismissCallback(block : () -> Unit) : EmailConfirmPasswordDialog {
        onDismiss = block
        return this
    }

    fun setOnSendAgainCallback(block : () -> Unit): EmailConfirmPasswordDialog {
        onSend = block
        return this
    }
}