package com.pagercalendar.calendar.format;

import androidx.annotation.NonNull;

import com.pagercalendar.calendar.CalendarDay;

public interface DayFormatter {

    String DEFAULT_FORMAT = "d";

    DayFormatter DEFAULT = new DateFormatDayFormatter();

    @NonNull
    String format(@NonNull CalendarDay day);
}