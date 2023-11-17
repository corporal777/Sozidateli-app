package com.pagercalendar.calendar.format;

import org.threeten.bp.DayOfWeek;

public interface WeekDayFormatter {
    CharSequence format(DayOfWeek dayOfWeek);
    WeekDayFormatter DEFAULT = new CalendarWeekDayFormatter();
}
