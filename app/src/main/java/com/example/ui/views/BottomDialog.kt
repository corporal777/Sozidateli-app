package com.example.ui.views

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import com.example.app.databinding.LayoutBottomDialogBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class BottomDialog : BottomSheetDialog {


    private val contentLayout = LayoutBottomDialogBinding.inflate(LayoutInflater.from(context))

    constructor(context: Context) : super(context)
    constructor(context: Context, theme: Int) : super(context, theme)

    init {
        setContentView(contentLayout.root)
        BottomSheetBehavior.from(contentLayout.root as View).apply {
            isHideable = false
        }
    }

    override fun setTitle(titleId: Int) {

        setTitle(context.getText(titleId))
    }

    override fun setTitle(title: CharSequence?) {
        contentLayout.tvTitle.apply {
            text = title
            isVisible = !title.isNullOrEmpty()
        }
    }

    fun setMessage(messageId: Int) {
        setMessage(context.getText(messageId))
    }

    fun setMessage(message: CharSequence?) {
        contentLayout.tvMessage.apply {
            text = message
            isVisible = !message.isNullOrEmpty()
        }
    }

    fun setButton(button: Button, message: String?, clickListener: (() -> Boolean)?) {
        val buttonView = when (button) {
            Button.POSITIVE -> contentLayout.btnPositive
            Button.NEGATIVE -> contentLayout.btnNegative
        }

        buttonView.apply {
            text = message
            isVisible = !message.isNullOrEmpty()
            setOnClickListener(if (clickListener == null) null else View.OnClickListener {
                if (clickListener()) dismiss()
            })
        }
    }

    fun positiveButton(buttonBuilder: ButtonBuilder.() -> Unit) {
        ButtonBuilder().apply(buttonBuilder).apply {
            setButton(Button.POSITIVE, text, clickListener)
        }
    }


    fun negativeButton(buttonBuilder: ButtonBuilder.() -> Unit) {
        ButtonBuilder().apply(buttonBuilder).apply {
            setButton(Button.NEGATIVE, text, clickListener)
        }
    }

    enum class Button {
        POSITIVE, NEGATIVE
    }

    class ButtonBuilder {
        var text: String? = null
        var clickListener: (() -> Boolean)? = null
    }
}