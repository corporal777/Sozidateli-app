package com.example.ui.views.calendarView;

import androidx.annotation.NonNull;


public interface OnDateLongClickListener {

    void onDateLongClick(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date);
}