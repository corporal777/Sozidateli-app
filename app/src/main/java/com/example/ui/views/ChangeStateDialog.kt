package com.example.ui.views

import android.app.Activity
import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.example.R
import com.example.databinding.DialogChangeStateBinding
import com.example.util.ClickableSpan

class ChangeStateDialog(val activity: Context, val type: StateType) {

    private var onChangeState: (isAccept: Boolean) -> Unit = {}

    private var onClick: (state: ClickType) -> Unit = {}
    private val mBinding = DialogChangeStateBinding.inflate(LayoutInflater.from(activity))

    private lateinit var alertDialog: AlertDialog
    val builder = AlertDialog.Builder(activity)

    init {
        builder.setView(mBinding.root)
        builder.setCancelable(false)
        when(type) {
            StateType.SUCCESS -> {
                mBinding.tvTitle.isVisible = true
                mBinding.tvMessage.text = activity.resources.getString(R.string.change_status_description)
                mBinding.btnPositive.text = activity.resources.getString(R.string.change_status_button)
            }
            StateType.BASE -> {
                mBinding.tvTitle.isVisible = false
                mBinding.btnPositive.text = activity.resources.getString(R.string.get_base)
                mBinding.tvMessage.text = SpannableString(activity.resources.getString(R.string.title_get_base)).apply {
                    val linkStart = 84
                    val linkEnd = length
                    setSpan(ClickableSpan {
                        onClick(ClickType.INFO)
                        alertDialog.dismiss()
                    }, linkStart, linkEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                }
                mBinding.tvMessage.setOnClickListener {
                    onClick(ClickType.INFO)
                    alertDialog.dismiss()
                }
            }
            StateType.MAX -> {
                mBinding.tvTitle.isVisible = false
                mBinding.btnPositive.text = activity.resources.getString(R.string.get_max)
                mBinding.tvMessage.text = SpannableString(activity.resources.getString(R.string.title_get_max)).apply {
                    val linkStart = 89
                    val linkEnd = length
                    setSpan(ClickableSpan {
                        onClick(ClickType.INFO)
                        alertDialog.dismiss()
                    }, linkStart, linkEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                }
                mBinding.tvMessage.setOnClickListener {
                    onClick(ClickType.INFO)
                    alertDialog.dismiss()
                }
            }
        }
        mBinding.btnPositive.setOnClickListener {
            when (type) {
                StateType.BASE -> onClick(ClickType.BASE)
                StateType.MAX -> onClick(ClickType.MAX)
                else -> onClick(ClickType.INFO)
            }
            onChangeState(true)
            alertDialog.dismiss()
        }
        mBinding.btnNegative.setOnClickListener {
            onChangeState(false)
            alertDialog.dismiss()
        }
        alertDialog = builder.create()
        alertDialog.show()
    }

    fun setClickCallback(block: (state: ClickType) -> Unit): ChangeStateDialog {
        onClick = block
        return this
    }
}

enum class StateType {
    SUCCESS, BASE, MAX
}

enum class ClickType {
    INFO, BASE, MAX
}