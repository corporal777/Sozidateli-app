package com.example.ui.views.calendarView;

interface DateRangeIndex {
    int getCount();
    int indexOf(CalendarDay day);
    CalendarDay getItem(int position);
}

