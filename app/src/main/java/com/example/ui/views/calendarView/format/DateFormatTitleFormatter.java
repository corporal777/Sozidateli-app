package com.example.ui.views.calendarView.format;

import com.example.ui.views.calendarView.CalendarDay;

import org.threeten.bp.format.DateTimeFormatter;

public class DateFormatTitleFormatter implements TitleFormatter {

    private final DateTimeFormatter dateFormat;

    public DateFormatTitleFormatter() {
        this(DateTimeFormatter.ofPattern(DEFAULT_FORMAT));
    }

    public DateFormatTitleFormatter(final DateTimeFormatter format) {
        this.dateFormat = format;
    }

    @Override public CharSequence format(final CalendarDay day) {
        return dateFormat.format(day.getDate());
    }
}
