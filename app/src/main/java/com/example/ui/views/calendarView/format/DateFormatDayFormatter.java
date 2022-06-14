package com.example.ui.views.calendarView.format;


import androidx.annotation.NonNull;

import com.example.ui.views.calendarView.CalendarDay;

import java.text.DateFormat;
import java.util.Locale;
import org.threeten.bp.format.DateTimeFormatter;

public class DateFormatDayFormatter implements DayFormatter {

    private final DateTimeFormatter dateFormat;

    public DateFormatDayFormatter() {
        this(DateTimeFormatter.ofPattern(DEFAULT_FORMAT, Locale.getDefault()));
    }

    public DateFormatDayFormatter(@NonNull final DateTimeFormatter format) {
        this.dateFormat = format;
    }

    @Override @NonNull public String format(@NonNull final CalendarDay day) {
        return dateFormat.format(day.getDate());
    }
}
