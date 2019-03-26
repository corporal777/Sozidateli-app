package com.example.holders.profile

import androidx.transition.AutoTransition
import androidx.transition.Transition
import androidx.transition.TransitionListenerAdapter
import androidx.transition.TransitionManager
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.data.models.ProfileField
import com.example.data.models.ProfileFieldExpand
import com.example.data.models.Type
import com.example.holders.ActionButtonItem
import com.example.ui.profile.profileEdit.ProfileEditPresenter
import com.example.util.FIELD_ATTACH_RECOMMENDATION_FILE
import com.example.util.Utils
import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.field_expand_profile.view.*
import android.widget.ImageView
import com.example.R


class ProfileExpandFieldItem(private val name: String, private var expandField: ProfileFieldExpand,
                             private val isLast: Boolean, private val presenter: ProfileEditPresenter? = null) : Item() {


    private val DP_MARGIN = 25

    private var isAnimationInProcess = false
    private var isExpanded = false

    private var globalSection = Section().apply {
        setFooter(getAddButton())
    }

    private var groupAdapter = GroupAdapter<ViewHolder>().apply {
        add(globalSection)
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.apply {
            setOnTouchListener { view, motionEvent ->
                if (isAnimationInProcess) return@setOnTouchListener true
                if (motionEvent.action == MotionEvent.ACTION_UP) {
                    if (isExpanded) {
                        animCollapse(fieldRecyclerView)
                        animArrowRotation(ivArrow,90f)
                        //ivArrow.rotation = 90f
                    } else {
                        animExpand(fieldRecyclerView)
                        animArrowRotation(ivArrow,270f)
                        //ivArrow.rotation = 270f
                    }
                }
                return@setOnTouchListener true
            }
            tvName.text = name
            fieldRecyclerView.apply {
                layoutManager = GridLayoutManager(context, 5).apply {
                    spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            var count = 0

                            if (groupAdapter.getItem(position) is ProfileBaseFieldItem) {
                                count = 4
                            } else if (groupAdapter.getItem(position) is TrashItem) {
                                count = 1
                            } else {
                                count = 5
                            }

                            return count
                        }
                    }
                }
                adapter = groupAdapter

                itemAnimator = null
            }

            update()

            if (isLast) {
                bottomDivider.visibility = View.VISIBLE
            } else {
                bottomDivider.visibility = View.GONE
            }
        }
    }

    private fun animArrowRotation(arrow: ImageView,setRotation:Float){
        arrow.animate().rotation(setRotation).setDuration(400).start()
    }

    fun updateExpandField(expandField: ProfileFieldExpand) {
        this@ProfileExpandFieldItem.expandField = expandField
        update()
    }

    private fun update() {
        val listOfField = mutableListOf<Group>()

        expandField.listOfField.forEach {
            listOfField.add(createSection(it, expandField.listOfField.indexOf(it)))
        }

        globalSection.update(listOfField)
    }

    private fun createSection(list: MutableList<ProfileField>, position: Int): Section {
        val section = Section()
        list.forEach {

            val baseFieldItem: ProfileBaseFieldItem

            when (it.type) {
                Type.DATE -> {
                    baseFieldItem = ProfileDatetItem(it)
                }
                Type.EMAIL -> {
                    baseFieldItem = ProfileEmailItem(it)
                }
                Type.PASSWORD -> {
                    baseFieldItem = ProfilePasswordItem(it)
                }
                Type.PHONE -> {
                    baseFieldItem = ProfilePhoneItem(it)
                }
                Type.SUPPORT -> {
                    baseFieldItem = ProfileSupportItem(it)
                }
                else -> {
                    baseFieldItem = ProfileTextItem(it)
                }
            }

            section.add(baseFieldItem)

            if (list.size / 2 == list.indexOf(it)) {
                section.add(TrashItem(View.OnClickListener {
                    expandField.listOfField.removeAt(position)
                    update()
                }))
            }
        }
        section.add(MarginItem(Utils.dpToPx(DP_MARGIN)))

        section.add(ProfileFieldDividerItem())

        return section
    }


    private fun getAddButton(): Section {
        val section = Section()

        section.add(ActionButtonItem("Добавить", View.OnClickListener {
            if (expandField.nameField == FIELD_ATTACH_RECOMMENDATION_FILE) {
                presenter?.let {
                    it.onUploadDocumentClick()
                }
            } else {
                val newList = Utils.getListFieldValueByMapDefault(null, expandField.defaultFields)
                expandField.listOfField.add(newList)

                update()
            }
        }, Utils.dpToPx(DP_MARGIN)))
        section.add(MarginItem(Utils.dpToPx(DP_MARGIN)))
        return section
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

    fun getFieldExpand() = expandField

    override fun getLayout() = R.layout.field_expand_profile
}