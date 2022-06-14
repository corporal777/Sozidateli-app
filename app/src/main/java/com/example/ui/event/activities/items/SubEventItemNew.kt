package com.example.ui.event.activities.items

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.R
import com.example.data.models.EventActivityModel
import com.example.data.models.Tag
import com.example.databinding.ItemSubEventNewBinding
import com.example.extensions.defaultServerDateTimeFormatter
import com.example.extensions.findGroupBy
import com.example.extensions.findItemBy
import com.example.holders.CalendarHorizontalListItem
import com.example.holders.redesign.EventActivityDateItem
import com.example.holders.redesign.EventActivityItem
import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.Section
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_lecture.*
import ru.ok.android.sdk.LOG_TAG
import java.util.*

class SubEventItemNew(
    private val emptyEventTitle: String,
    private val noEventWithParamsTitle: String,
    private var subEvents: Map<String, List<EventActivityModel>>,
    val selectedTags: List<Tag>,
    private val clickListener: EventActivityItem.OnEventActivityClickListener
) : BindableItem<ItemSubEventNewBinding>() {

    private val eventSection = Section()
    val listItem = arrayListOf<SubEventsWithDateItem>()
    private lateinit var mGroup: SubEventsWithDateItem


    private val groupAdapter by lazy {
        GroupAdapter<GroupieViewHolder>().apply {
            add(eventSection)
            //updateAsync(listItem)
        }
    }

    init {
        if (!subEvents.isNullOrEmpty()) {
            subEvents.map {
                eventSection.add(
                    SubEventsWithDateItem(
                        it.key,
                        it.value,
                        selectedTags,
                        clickListener
                    )
                )
            }
        }

    }

    override fun bind(viewBinding: ItemSubEventNewBinding, position: Int) {
        viewBinding.apply {
            listSubEvents.adapter = groupAdapter
        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (other !is SubEventItemNew) return false
        if (subEvents != other.subEvents) return false
        if (selectedTags != other.selectedTags) return false
        return true
    }

    override fun bind(
        viewBinding: ItemSubEventNewBinding,
        position: Int,
        payloads: MutableList<Any>?
    ) {
        val payload = payloads?.firstOrNull()
        if (payload == null) super.bind(viewBinding, position, payloads)
        else {
            if (payload is Map<*, *>) {
                this.subEvents = payload as Map<String, List<EventActivityModel>>
                delete(payload)
            }
        }
    }


    fun delete(subEvents: Map<String, List<EventActivityModel>>) {
        subEvents.map {
            try {
                val group =
                    eventSection.findGroupBy<SubEventsWithDateItem> { x -> x.date == it.key }
                if (it.key == group?.date) {
                    eventSection.remove(group)
                }
            } catch (e: Exception) {

            }


        }
    }

    fun add(subEvents: Map<String, List<EventActivityModel>>) {
        val sorted: MutableMap<String, List<EventActivityModel>> = TreeMap(subEvents)

        if (!subEvents.isNullOrEmpty()) {
            var mPosition = 0
            subEvents.map {
                eventSection.add(
                    mPosition,
                    SubEventsWithDateItem(
                        it.key,
                        it.value,
                        selectedTags,
                        clickListener
                    )
                )
                mPosition += 1
            }

        }
    }


    override fun getLayout(): Int = R.layout.item_sub_event_new

}