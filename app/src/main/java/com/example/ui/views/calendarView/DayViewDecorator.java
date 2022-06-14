package com.example.ui.views.calendarView;

public interface DayViewDecorator {

    boolean shouldDecorate(CalendarDay day);
    void decorate(DayViewFacade view);
}
