package com.example.ui.views

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import com.example.R
import com.example.databinding.DialogConfirmCodeBinding
import com.example.util.Utils
import me.saket.bettermovementmethod.BetterLinkMovementMethod

class ConfirmCodeDialog(val email: String, val context: Context, val type: RegisterDataType) {

    private val timerEmailMessage by lazy { context.getString(R.string.auth_register_confirm_email_timer_two) }
    private val timerPhoneMessage by lazy { context.getString(R.string.auth_register_confirm_phone_timer) }

    private val mBinding = DialogConfirmCodeBinding.inflate(LayoutInflater.from(context))

    private var clickConfirm: (code : String) -> Unit = {}
    private var clickSendAgain: (email : String) -> Unit = {}

    private lateinit var mAlertDialog: AlertDialog
    private val mBuilder = AlertDialog.Builder(context)

    init {
        mBuilder.setView(mBinding.root)
        mBuilder.setCancelable(false)

        when (type) {
            RegisterDataType.EMAIL -> {
                mBinding.apply {
                    tvMessage.text = context.getString(R.string.code_email_dialog_text, email)
                    etCode.setHint(R.string.code_email_input_label)
                    btnResend.text = context.getString(R.string.send_code_again)
                }
            }
            RegisterDataType.PHONE -> {
                mBinding.apply {
                    tvMessage.apply {
                        movementMethod = BetterLinkMovementMethod.getInstance()
                        text = context.getString(R.string.call_code_phone_dialog_text)
                    }
                    btnResend.text = context.getString(R.string.send_call_again)
                    etCode.setHint(R.string.code_phone_input_label)
                }
            }
        }

        mBinding.btnPositive.setOnClickListener {
            clickConfirm.invoke(mBinding.etCode.text.toString())
            mAlertDialog.dismiss()
        }
        mBinding.btnNegative.setOnClickListener {
            mAlertDialog.dismiss()
        }
        mBinding.btnResend.apply {
            setTextColor(
                ColorStateList(
                    arrayOf(
                        intArrayOf(android.R.attr.state_enabled),
                        intArrayOf(-android.R.attr.state_enabled)
                    ),
                    intArrayOf(
                        ContextCompat.getColor(context, R.color.colorAccent),
                        ContextCompat.getColor(context, R.color.input_text_color_disabled_new)
                    )
                )
            )
            setOnClickListener { clickSendAgain.invoke(email) }
        }

        mAlertDialog = mBuilder.create()
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 20)
        mAlertDialog.window?.setBackgroundDrawable(inset)
        mAlertDialog.show()
    }

    fun setTimeLeft(time: Int) {
        val quantity = Utils.timerFormatter(time, context)
        val desc =  when (type) {
            RegisterDataType.EMAIL -> String.format(timerEmailMessage, quantity)
            RegisterDataType.PHONE -> String.format(timerPhoneMessage, quantity)
            else -> ""
        }
        val visible = time <= 0
        mBinding.btnResend.isEnabled = visible
        mBinding.tvTimer.apply {
            isInvisible = visible
            text = desc
        }
    }

    fun setConfirmCallback(block: (code : String) -> Unit): ConfirmCodeDialog {
        clickConfirm = block
        return this
    }

    fun setSendAgainCallback(block: (code : String) -> Unit): ConfirmCodeDialog {
        clickSendAgain = block
        return this
    }
}