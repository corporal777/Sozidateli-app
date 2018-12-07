package com.example.holders

import android.support.transition.AutoTransition
import android.support.transition.Transition
import android.support.transition.TransitionListenerAdapter
import android.support.transition.TransitionManager
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.example.R
import com.example.data.models.ProfileField
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.field_expand_profile.view.*

class ProfileExpandFieldItem(private val name:String, private val mutableList: MutableList<ProfileFieldItem>,private val isLast:Boolean) : Item() {

    private var isAnimationInProcess = false
    private var isExpanded = false
    private var groupAdapter = GroupAdapter<ViewHolder>()

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.apply {
            tvName.setOnTouchListener {view,motionEvent->
                if(isAnimationInProcess) return@setOnTouchListener true
                if(motionEvent.action == MotionEvent.ACTION_UP) {
                    if (isExpanded) {
                        animCollapse(fieldRecyclerView)
                    } else {
                        animExpand(fieldRecyclerView)
                    }
                }
                return@setOnTouchListener true
            }
            tvName.text = name
            fieldRecyclerView.apply {
                layoutManager  = LinearLayoutManager(context)
                adapter = groupAdapter
            }
            groupAdapter.update(mutableList)
            if(isLast){
                bottomDivider.visibility = View.VISIBLE
            } else {
                bottomDivider.visibility = View.GONE
            }
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

    fun getFieldItems() = mutableList

    override fun getLayout() = R.layout.field_expand_profile
}