package com.example.ui.views.dialogs_new


import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import com.example.R
import com.example.databinding.BottomSheetCalendarBinding
import com.example.extensions.dp
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import setOnClickListener
import java.util.*


class CalendarBottomSheet(private val context: Context) {

    private val mBinding = BottomSheetCalendarBinding.inflate(LayoutInflater.from(context))

//    private val calendarSection = Section()
//    private val groupAdapter by lazy {
//        GroupAdapter<GroupieViewHolder>().apply {
//            add(calendarSection)
//        }
//    }

    private var mDialog = BottomSheetDialog(context)

    init {
        mDialog.setContentView(mBinding.root)

        val calendar = Calendar.getInstance()
//        val day = EventScheduleCalendarDay(
//            calendar.timeInMillis,
//            calendar.get(Calendar.WEEK_OF_MONTH),
//            calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
//                ?: "",
//            calendar.get(Calendar.DAY_OF_MONTH),
//            false
//        )
//        calendarSection.update(listOf(CalendarItem(day)))

//        mBinding.calendarPager.apply {
//            offscreenPageLimit = 3
//            adapter = groupAdapter
//        }

//        val back = ColorDrawable(Color.TRANSPARENT)
//        val inset = InsetDrawable(back, 50)
//        mDialog.window?.setBackgroundDrawable(inset)

        mBinding.calendarView.apply {
            tileWidth = 50.dp
            tileHeight = 40.dp
            setTitleMonths(R.array.custom_months)
            setOnDateChangedListener { widget, date, selected ->
                Toast.makeText(context, date.date.toString(), Toast.LENGTH_SHORT).show()
            }
        }
        mDialog.show()

        mBinding.btnClose.setOnClickListener {
            mDialog.dismiss()
        }
    }
}