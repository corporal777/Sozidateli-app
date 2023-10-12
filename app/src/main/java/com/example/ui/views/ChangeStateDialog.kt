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

class ChangeStateDialog(val activity: Activity, val type: StateType) {

    private var onChangeState: (isAccept: Boolean) -> Unit = {}

    private var onClick: (state: ClickType) -> Unit = {}

    var binding : DialogChangeStateBinding = DataBindingUtil.inflate(
        activity.layoutInflater,
        R.layout.dialog_change_state,
        null,
        false
    )

    private lateinit var alertDialog: AlertDialog
    val builder = AlertDialog.Builder(activity)

    init {
        builder.setView(binding.root)
        builder.setCancelable(false)
        when(type) {
            StateType.SUCCESS -> {
                binding.tvTitle.isVisible = true
                binding.tvMessage.text = activity.resources.getString(R.string.change_status_description)
                binding.btnPositive.text = activity.resources.getString(R.string.change_status_button)
            }
            StateType.BASE -> {
                binding.tvTitle.isVisible = false
                binding.btnPositive.text = activity.resources.getString(R.string.get_base)
                binding.tvMessage.text = SpannableString(activity.resources.getString(R.string.title_get_base)).apply {
                    val linkStart = 84
                    val linkEnd = length
                    setSpan(ClickableSpan {
                        onClick(ClickType.INFO)
                        alertDialog.dismiss()
                    }, linkStart, linkEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                }
                binding.tvMessage.setOnClickListener {
                    onClick(ClickType.INFO)
                    alertDialog.dismiss()
                }
            }
            StateType.MAX -> {
                binding.tvTitle.isVisible = false
                binding.btnPositive.text = activity.resources.getString(R.string.get_max)
                binding.tvMessage.text = SpannableString(activity.resources.getString(R.string.title_get_max)).apply {
                    val linkStart = 89
                    val linkEnd = length
                    setSpan(ClickableSpan {
                        onClick(ClickType.INFO)
                        alertDialog.dismiss()
                    }, linkStart, linkEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                }
                binding.tvMessage.setOnClickListener {
                    onClick(ClickType.INFO)
                    alertDialog.dismiss()
                }
            }
            else -> {
                binding.tvTitle.isVisible = false
                binding.tvMessage.text = activity.resources.getString(R.string.edit_status_description)
                binding.btnPositive.text = activity.resources.getString(R.string.change_status_button)
            }
        }
        binding.btnPositive.setOnClickListener {
            when (type) {
                StateType.BASE -> {
                    onClick(ClickType.BASE)
                }
                StateType.MAX -> {
                    onClick(ClickType.MAX)
                }
                else -> {
                    onClick(ClickType.INFO)
                }
            }
            onChangeState(true)
            alertDialog.dismiss()
        }
        binding.btnNegative.setOnClickListener {
            onChangeState(false)
            alertDialog.dismiss()
        }
        alertDialog = builder.create()
        alertDialog.show()
    }

    fun setSendCodeCallback(block: (isAccept: Boolean) -> Unit): ChangeStateDialog {
        onChangeState = block
        return this
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