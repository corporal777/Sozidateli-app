package com.example.ui.views

import android.app.Activity
import android.content.res.ColorStateList
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import com.example.R
import com.example.databinding.DialogAddPhoneEmailBinding
import com.example.ui.auth.register.email.finish.FinishRegisterPresenter
import com.example.util.AuthValidateUtil
import com.example.util.Utils
import com.example.util.Utils.timerFormatter
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.fragment_finish_register.*
import performOnBackgroundOutOnMain
import java.util.concurrent.TimeUnit

class AddPhoneEmailDialog(val activity: Activity, val type: RegisterDataType) {

    private var onSelect: (result: PhoneEmailResult) -> Unit = {}
    private var onSendCode: () -> Unit = {}
    private var onNegativeClick: () -> Unit = {}
    private val timerCompositeDisposable = CompositeDisposable()
    private val timerMessage by lazy {
        activity.resources.getString(R.string.auth_register_confirm_email_timer_two)
    }

    var binding : DialogAddPhoneEmailBinding = DataBindingUtil.inflate(
            activity.layoutInflater,
            R.layout.dialog_add_phone_email,
            null,
            false
    )

    private lateinit var alertDialog: AlertDialog
    val builder = AlertDialog.Builder(activity)

    init {
        builder.setView(binding.root)
        binding.btnPositive.setOnClickListener {
            //isProgressVisible(true)
            onSelect.invoke(PhoneEmailResult(type, binding.etLogin.text.toString()))
        }
        binding.tvCode.apply {
            setTextColor(ColorStateList(
                    arrayOf(intArrayOf(android.R.attr.state_enabled), intArrayOf(-android.R.attr.state_enabled)),
                    intArrayOf(ContextCompat.getColor(activity, R.color.colorAccent), ContextCompat.getColor(activity, R.color.action_button_disabled_text_color))
            ))
            setOnClickListener {
                onSendCode()
                startTimer()
            }
        }
        binding.btnPositive.isEnabled = false
        binding.etLogin.doAfterTextChanged {
            when (type) {
                RegisterDataType.EMAIL -> {
                    binding.btnPositive.isEnabled = AuthValidateUtil.isValidEmail(it.toString())
                }
                RegisterDataType.PHONE, RegisterDataType.CHANGE_PHONE -> {
                    binding.btnPositive.isEnabled = Utils.newPhoneValidator(it.toString())
                }
                RegisterDataType.CODE -> {
                    binding.btnPositive.isEnabled = it.toString().length == CODE_SIZE
                }
            }
        }
        binding.btnNegative.setOnClickListener {
            onNegativeClick()
            alertDialog.dismiss()
        }
        setData(null)
        alertDialog = builder.create()
        alertDialog.show()
    }

    private fun setData(phone: String?) {
        when (type) {
            RegisterDataType.EMAIL -> {
                binding.tvTitle.text = activity.resources.getString(R.string.add_email_dialog_title)
                binding.tvMessage.text = activity.resources.getString(R.string.add_email_dialog_text)
                binding.etLogin.setHint(R.string.email)
                binding.tvCode.isVisible = false
            }
            RegisterDataType.PHONE -> {
                binding.tvTitle.text = activity.resources.getString(R.string.add_phone_dialog_title)
                binding.tvMessage.text = activity.resources.getString(R.string.add_phone_dialog_text)
                binding.etLogin.setHint(R.string.search_filter_phone)
                binding.tvCode.isVisible = false
            }
            RegisterDataType.CODE -> {
                startTimer()
                binding.tvTitle.text = activity.resources.getString(R.string.code_dialog_title)
                binding.tvMessage.text = activity.resources.getString(R.string.code_phone_dialog_text, phone)
                binding.etLogin.setHint(R.string.enter_code_btn_text)
                binding.tvCode.isVisible = true
            }
            RegisterDataType.CHANGE_PHONE -> {
                binding.tvTitle.text = activity.resources.getString(R.string.new_phone_number)
                binding.tvMessage.text = activity.resources.getString(R.string.add_phone_dialog_text)
                binding.etLogin.setHint(R.string.search_filter_phone)
                binding.tvCode.isVisible = false
            }
        }
    }

    fun isProgressVisible(vis: Boolean) {
        binding.progressBar2.isVisible = vis
    }

    fun setPhoneForCode(phone: String) {
        setData(phone)
    }

    fun setSelectCallback(block: (result: PhoneEmailResult) -> Unit): AddPhoneEmailDialog {
        onSelect = block
        return this
    }

    fun hideDialog() {
        alertDialog.dismiss()
    }

    fun setSendCodeCallback(block: () -> Unit): AddPhoneEmailDialog {
        onSendCode = block
        return this
    }

    fun setNegativeClickCallback(block: () -> Unit): AddPhoneEmailDialog {
        onNegativeClick = block
        return this
    }

    private fun startTimer() {
        timerCompositeDisposable.clear()
        setCanResend(false)
        setTimeLeft(FinishRegisterPresenter.TIMER_SECONDS_COUNT)

        timerCompositeDisposable += Observable.interval(1000, TimeUnit.MILLISECONDS)
                .performOnBackgroundOutOnMain()
                .subscribe({
                    val timeLeft = FinishRegisterPresenter.TIMER_SECONDS_COUNT - (it.toInt() + 1)
                    if (timeLeft < 0) {
                        timerCompositeDisposable.clear()
                        setCanResend(true)
                    } else {
                        setTimeLeft(timeLeft)
                    }
                }, {
                    it.printStackTrace()
                })
    }

    fun setTimeLeft(seconds: Int) {
        //val quantity = activity.resources.getQuantityString(R.plurals.seconds_timer, seconds, seconds)
        val quantity = timerFormatter(seconds, activity)
        binding.tvTimer.text = String.format(timerMessage, quantity)
    }

    fun setCanResend(canResend: Boolean) {
        binding.tvCode.isEnabled = canResend
        binding.tvTimer.isVisible = !canResend
    }

    companion object {
        const val CODE_SIZE = 6
    }
}

data class PhoneEmailResult(
        val type: RegisterDataType,
        val value: String
)

enum class RegisterDataType {
    EMAIL, PHONE, CODE, CHANGE_PHONE
}