package com.example.ui.views

import android.content.Context
import android.content.DialogInterface.BUTTON_NEGATIVE
import android.content.DialogInterface.BUTTON_POSITIVE
import androidx.core.content.ContextCompat
import com.example.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ClearDegreeDialog(val context: Context) {

    private var onSelect: (isAgree: Boolean) -> Unit = {}

    init {
        val dialog = MaterialAlertDialogBuilder(context)
                .setMessage(R.string.change_degree_text)
                .setNegativeButton(context.resources.getString(R.string.nope)) { dialog, _ ->
                    onSelect.invoke(false)
                    dialog.dismiss()
                }
                .setPositiveButton(context.resources.getString(R.string.yes)) { dialog, _ ->
                    onSelect.invoke(true)
                    dialog.dismiss()
                }.show()
        dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.dialog_buttons))
        dialog.getButton(BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.dialog_buttons))
    }

    fun setSelectCallback(block: (isAgree: Boolean) -> Unit): ClearDegreeDialog {
        onSelect = block
        return this
    }
}