package com.example.ui.views.dialogs_new

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.R
import com.example.databinding.BottomSheetCalendarBinding
import com.example.extensions.dp
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CalendarBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding : BottomSheetCalendarBinding? = null
    private val mBinding get() = _binding!!
    private var bottomSheetBehavior: BottomSheetBehavior<*>? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheet: BottomSheetDialog =
            super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        val view: View = View.inflate(requireContext(), R.layout.bottom_sheet_calendar, null)
        bottomSheet.setContentView(view)
        bottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
        (bottomSheetBehavior as BottomSheetBehavior<*>).peekHeight =
            BottomSheetBehavior.PEEK_HEIGHT_AUTO;

        return bottomSheet
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomSheetCalendarBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.calendarView.apply {
            tileWidth = 50.dp
            tileHeight = 40.dp
            setTitleMonths(R.array.custom_months)
            setOnDateChangedListener { widget, date, selected ->
                Toast.makeText(context, date.date.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        mBinding.btnClose.setOnClickListener {
            this.dismiss()
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}