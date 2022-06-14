package com.example.ui.views.calendarView.format;

import androidx.annotation.NonNull;

import com.example.ui.views.calendarView.CalendarDay;

public interface DayFormatter {

    String DEFAULT_FORMAT = "d";

    DayFormatter DEFAULT = new DateFormatDayFormatter();

    @NonNull
    String format(@NonNull CalendarDay day);
}