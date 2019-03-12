package com.example.ui.views

import android.content.Context
import android.graphics.Typeface
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import androidx.transition.AutoTransition
import androidx.transition.Transition
import androidx.transition.TransitionListenerAdapter
import androidx.transition.TransitionManager
import androidx.appcompat.view.ContextThemeWrapper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
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

    lateinit var typeface: Typeface

    private fun init() {
        view = LayoutInflater.from(context).inflate(R.layout.expended_info_view, this, true)
        view.tvName.setOnClickListener {
            if (isAnimationInProcess) return@setOnClickListener
            if (isExpanded) {
                animCollapse(view.llContainerInfo)
                animArrowRotation(ivArrow,90f)
            } else {
                animExpand(view.llContainerInfo)
                animArrowRotation(ivArrow,270f)
            }
        }
        typeface = Typeface.createFromAsset(context.assets, "fonts/OpenSans-Light.ttf")
    }

    fun setName(name: String?) {
        name?.let {
            view.tvName.text = it
        }
    }

    fun setDataInfo(arrayInfo: MutableList<HashMap<String, String?>>) {
        if (view.llContainerInfo.childCount == 0) {
            arrayInfo.forEach {
                createField(it, arrayInfo.indexOf(it) == arrayInfo.size - 1)
            }
        }
    }

    private fun createField(info: HashMap<String, String?>, isLast: Boolean) {
        info.forEach { (key, value) ->
            val ll = LinearLayout(ContextThemeWrapper(context, R.style.ProfileInfoField))
            // val layParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)

            if (key.isNotEmpty() && !value.isNullOrEmpty()) {
                val label = TextView(ContextThemeWrapper(context, R.style.ProfileInfoField_Label))
                label.typeface = typeface
                label.text = key
                ll.addView(label)
            }

            if (!value.isNullOrEmpty()) {
                val text = TextView(ContextThemeWrapper(context, R.style.ProfileInfoField_Text))
                text.text = value
                text.linksClickable = true
                text.autoLinkMask = Linkify.WEB_URLS
                text.movementMethod = LinkMovementMethod.getInstance()
                ll.addView(text)
            }

            val layParamsLL = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            view.llContainerInfo.addView(ll, layParamsLL)
        }

        if (!isLast) {
            val dividerParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, context.resources.getDimensionPixelSize(R.dimen.profile_full_height_divider))
            val divider = View(context)
            dividerParams.topMargin = context.resources.getDimensionPixelSize(R.dimen.profile_full_padding_top)
            divider.setBackgroundResource(R.color.colorDivider)
            view.llContainerInfo.addView(divider, dividerParams)
        }

    }

    fun setisFirstExpand(isFirst: Boolean) {
        val marginTop = if (isFirst) context.resources.getDimensionPixelSize(R.dimen.profile_first_expand_margin_top) else 0
        view.topDivider.visibility = if (isFirst) View.VISIBLE else View.GONE
        (view.root.layoutParams as FrameLayout.LayoutParams).topMargin = marginTop
    }

    private fun animArrowRotation(arrow: ImageView, setRotation:Float){
        arrow.animate().rotation(setRotation).setDuration(400).start()
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