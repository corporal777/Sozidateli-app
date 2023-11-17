package com.pagercalendar.calendar.format;

import org.threeten.bp.DayOfWeek;

public class ArrayWeekDayFormatter implements WeekDayFormatter {

    private final CharSequence[] weekDayLabels;

    public ArrayWeekDayFormatter(final CharSequence[] weekDayLabels) {
        if (weekDayLabels == null) {
            throw new IllegalArgumentException("Cannot be null");
        }
        if (weekDayLabels.length != 7) {
            throw new IllegalArgumentException("Array must contain exactly 7 elements");
        }
        this.weekDayLabels = weekDayLabels;
    }

    @Override
    public CharSequence format(final DayOfWeek dayOfWeek) {
        return weekDayLabels[dayOfWeek.getValue() - 1];
    }
}
