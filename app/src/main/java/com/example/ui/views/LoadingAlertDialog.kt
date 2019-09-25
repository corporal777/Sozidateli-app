package com.example.ui.views

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.example.R

class LoadingAlertDialog(
        val sourceDialog: AlertDialog
) {

    private val loadingView by lazy {
        LayoutInflater.from(sourceDialog.context).inflate(R.layout.layout_loading, null)
    }

    fun showLoading(show: Boolean) {
        if (loadingView.parent == null) {
            (sourceDialog.window?.decorView as? ViewGroup)?.addView(loadingView)
        }

        loadingView.isVisible = show
    }

    companion object {
        fun createFrom(sourceDialog: AlertDialog): LoadingAlertDialog = LoadingAlertDialog(sourceDialog)
    }
}