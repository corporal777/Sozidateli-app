package com.example.ui.views.calendarView;

import androidx.annotation.NonNull;

public interface OnDateSelectedListener {
    void onDateSelected(
            @NonNull MaterialCalendarView widget,
            @NonNull CalendarDay date,
            boolean selected);
}
