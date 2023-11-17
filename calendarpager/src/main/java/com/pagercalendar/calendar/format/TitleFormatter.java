package com.pagercalendar.calendar.format;

import com.pagercalendar.calendar.CalendarDay;

public interface TitleFormatter {

    String DEFAULT_FORMAT = "LLLL yyyy";

    TitleFormatter DEFAULT = new DateFormatTitleFormatter();

    CharSequence format(CalendarDay day);
}
