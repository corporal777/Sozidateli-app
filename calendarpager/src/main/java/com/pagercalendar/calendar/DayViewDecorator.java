package com.pagercalendar.calendar;

public interface DayViewDecorator {

    boolean shouldDecorate(CalendarDay day);
    void decorate(DayViewFacade view);
}
