package com.example.ui.views.calendarView.format;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.format.TextStyle;

import java.util.Locale;

public final class CalendarWeekDayFormatter implements WeekDayFormatter {
    @Override public CharSequence format(final DayOfWeek dayOfWeek) {
        return dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault());
    }
}
