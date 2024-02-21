package com.example.ui.event.agreement

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialog
import com.example.App
import com.example.R
import com.example.data.models.SnAuth
import com.example.databinding.DialogEventRegistrationAgreementBinding
import com.example.ui.event.my.schedule.calendar.CalendarBottomSheet
import com.example.ui.event.my.schedule.calendar.CalendarBottomSheetPresenter
import com.example.util.ClickableSpanNew
import com.example.util.showCustomTabsBrowser
import io.reactivex.subjects.SingleSubject
import moxy.MvpDelegate
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import java.lang.NullPointerException
import javax.inject.Inject
import javax.inject.Provider


class EventAgreementRegisterDialog(val context: Context, val url: String) {

    private var onSelect: () -> Unit = {}

    private var mBinding = DialogEventRegistrationAgreementBinding.inflate(LayoutInflater.from(context))

    private lateinit var mAlertDialog: AlertDialog
    private val mBuilder = AlertDialog.Builder(context)

    init {
        mBuilder.setView(mBinding.root)
        mBuilder.setCancelable(true)

        mBinding.tvAgree.apply {
            text = SpannableString(context.getString(R.string.auth_agree_user_agreement)).apply {
                val linkStart = 11
                val linkEnd = length
                setSpan(ClickableSpanNew(mBinding.tvAgree) {
                    showCustomTabsBrowser(context, url)
                }, linkStart, linkEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            }
            movementMethod = LinkMovementMethod.getInstance()
        }

        mBinding.btnOk.apply {
            isEnabled = false
            setOnClickListener {
                onSelect.invoke()
                mAlertDialog.dismiss()
            }
        }

        mBinding.btnCancel.setOnClickListener {
            mAlertDialog.dismiss()
        }

        mBinding.cbAgree.setOnCheckedChangeListener { _, checked ->
            mBinding.btnOk.isEnabled = checked
        }

        mAlertDialog = mBuilder.create()
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 10)
        mAlertDialog.window?.setBackgroundDrawable(inset)
        mAlertDialog.show()
    }


    fun setSelectCallback(block: () -> Unit): EventAgreementRegisterDialog {
        onSelect = block
        return this
    }

    companion object {
        private const val AGREEMENT_DIALOG_TAG = "agreement_dialog_tag"
    }

}