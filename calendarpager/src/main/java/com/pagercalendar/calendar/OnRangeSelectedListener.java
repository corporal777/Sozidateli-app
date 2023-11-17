package com.pagercalendar.calendar;

import androidx.annotation.NonNull;

import java.util.List;

public interface OnRangeSelectedListener {

    void onRangeSelected(@NonNull MaterialCalendarView widget, @NonNull List<CalendarDay> dates);
}
