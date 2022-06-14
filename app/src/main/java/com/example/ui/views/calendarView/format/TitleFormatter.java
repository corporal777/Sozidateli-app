package com.example.ui.views.calendarView.format;

import com.example.ui.views.calendarView.CalendarDay;

public interface TitleFormatter {

    String DEFAULT_FORMAT = "LLLL yyyy";

    TitleFormatter DEFAULT = new DateFormatTitleFormatter();

    CharSequence format(CalendarDay day);
}
