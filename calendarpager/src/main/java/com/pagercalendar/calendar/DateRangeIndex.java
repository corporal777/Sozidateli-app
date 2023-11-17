package com.pagercalendar.calendar;

interface DateRangeIndex {
    int getCount();
    int indexOf(CalendarDay day);
    CalendarDay getItem(int position);
}

