package com.example.ui.views.educationlist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.app.R
import com.example.extensions.settings

class EducationPopupWindow(val context: Context, val data: List<String>): PopupWindow() {

    private var popupWindow: PopupWindow
    private var currentSelection: String? = null
    var list: RecyclerView
    private var educationClickCallback: (item: String) -> Unit = {}
    private var getOldEducation: (education: EducationChangeModel) -> Unit = {}
    private var popupAdapter =
            PopupWindowAdapter(data) {
                if (isTrigger(it)) {
                    educationClickCallback.invoke(it)
                } else {

                }
            }

    init {
        val popupView = LayoutInflater.from(context).inflate(R.layout.popup_menu, null, false)
        popupWindow = PopupWindow(popupView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        popupWindow.settings()
        list = popupView.findViewById(R.id.transports_list)
        val layout = popupView.findViewById<RelativeLayout>(R.id.layout)
        layout.setOnClickListener {
            popupWindow.dismiss()
        }
        list.adapter = popupAdapter
    }

    fun setEducationClickCallback(block: (item: String) -> Unit): EducationPopupWindow {
        educationClickCallback = block
        return this
    }

    fun getOldEducationCallback(block: (education: EducationChangeModel) -> Unit): EducationPopupWindow {
        getOldEducation = block
        return this
    }

    fun hidePopup() {
        popupWindow.dismiss()
    }

    fun showPopup(view: View) {
        popupWindow.showAsDropDown(view, 0, 0)
    }

    fun updateSelection(education: String) {
        educationClickCallback.invoke(education)
    }

    fun setCurrentSelection(selection: String) {
        currentSelection = selection
    }

    private fun isTrigger(text: String?): Boolean {
        return if (text == "Более одного высшего" || text == "Высшее")
            currentSelection != "Более одного высшего" || currentSelection != "Высшее"
        else
            !(currentSelection == "Более одного высшего" || currentSelection == "Высшее")
    }
}

data class EducationChangeModel(val isAgree: Boolean, val newEducation: String)