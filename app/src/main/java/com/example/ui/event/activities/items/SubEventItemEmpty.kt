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

class SubEventItemEmpty(
) : BindableItem<ItemSubEventNewBinding>() {


    override fun bind(viewBinding: ItemSubEventNewBinding, position: Int) {
        viewBinding.apply {
        }
    }



    override fun getLayout(): Int = R.layout.item_sub_event_new

}