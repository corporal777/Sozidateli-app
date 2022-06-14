package com.example.ui.views.calendarView.format;

import org.threeten.bp.DayOfWeek;

public interface WeekDayFormatter {
    CharSequence format(DayOfWeek dayOfWeek);
    WeekDayFormatter DEFAULT = new CalendarWeekDayFormatter();
}
