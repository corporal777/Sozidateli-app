package com.example.ui.views.calendarView.format;

import android.text.SpannableStringBuilder;

import com.example.ui.views.calendarView.CalendarDay;

public class MonthArrayTitleFormatter implements TitleFormatter {

    private final CharSequence[] monthLabels;

    public MonthArrayTitleFormatter(CharSequence[] monthLabels) {
        if (monthLabels == null) {
            throw new IllegalArgumentException("Label array cannot be null");
        }
        if (monthLabels.length < 12) {
            throw new IllegalArgumentException("Label array is too short");
        }
        this.monthLabels = monthLabels;
    }

    @Override
    public CharSequence format(CalendarDay day) {
        return new SpannableStringBuilder()
                .append(monthLabels[day.getMonth() - 1])
                .append(" ")
                .append(String.valueOf(day.getYear()));
    }
}
