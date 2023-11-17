package com.pagercalendar.calendar;

import androidx.annotation.NonNull;

public interface OnDateSelectedListener {
    void onDateSelected(
            @NonNull MaterialCalendarView widget,
            @NonNull CalendarDay date,
            boolean selected);
}
