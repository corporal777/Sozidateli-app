package com.example.ui.views

import android.content.Context
import android.support.transition.AutoTransition
import android.support.transition.Transition
import android.support.transition.TransitionListenerAdapter
import android.support.transition.TransitionManager
import android.support.v7.view.ContextThemeWrapper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.example.R
import kotlinx.android.synthetic.main.expended_info_view.view.*

class ExpandedInfo : FrameLayout {

    private lateinit var view: View
    private var isAnimationInProcess = false
    private var isExpanded = false

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }


    private fun init() {
        view = LayoutInflater.from(context).inflate(R.layout.expended_info_view, this, true)
        view.tvName.setOnClickListener {
            if (isAnimationInProcess) return@setOnClickListener
            if(isExpanded){
                animCollapse(view.llContainerInfo)
            } else{
                animExpand(view.llContainerInfo)
            }
        }
    }

    fun setName(name: String?) {
        name?.let {
            view.tvName.text = it
        }
    }

    fun setDataInfo(info: HashMap<String, String>) {
        info.forEach { (key, value) ->
            val ll = LinearLayout(ContextThemeWrapper(context, R.style.ProfileInfoField))
           // val layParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)

            if (key.isNotEmpty()) {
                val label = TextView(ContextThemeWrapper(context, R.style.ProfileInfoField_Label))
                label.text = key
                ll.addView(label)
            }

            val text = TextView(ContextThemeWrapper(context, R.style.ProfileInfoField_Text))
            text.text = value
            ll.addView(text)

            val layParamsLL = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            view.llContainerInfo.addView(ll, layParamsLL)
        }
    }


    fun animExpand(v: View) {
        isAnimationInProcess = true
        TransitionManager.beginDelayedTransition(v.rootView as ViewGroup, AutoTransition().addListener(
                object : TransitionListenerAdapter() {
                    override fun onTransitionEnd(transition: Transition) {
                        isExpanded = true
                        isAnimationInProcess = false
                    }
                }
        ))
        if (!v.isShown) {
            v.visibility = View.VISIBLE
        } else {
            isExpanded = true
        }
    }

    fun animCollapse(v: View) {
        isAnimationInProcess = true
        TransitionManager.beginDelayedTransition(v.rootView as ViewGroup, AutoTransition().addListener(
                object : TransitionListenerAdapter() {
                    override fun onTransitionEnd(transition: Transition) {
                        isExpanded = false
                        isAnimationInProcess = false
                    }
                }
        ))
        if (v.isShown) {
            v.visibility = View.GONE
        } else {
            isExpanded = false
        }
    }
}