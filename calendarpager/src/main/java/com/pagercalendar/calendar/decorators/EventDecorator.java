package com.pagercalendar.calendar.decorators;

import com.pagercalendar.calendar.CalendarDay;
import com.pagercalendar.calendar.DayViewDecorator;
import com.pagercalendar.calendar.DayViewFacade;
import com.pagercalendar.calendar.spans.DotSpan;

import java.util.HashSet;
import java.util.List;

public class EventDecorator implements DayViewDecorator {


    private final HashSet<CalendarDay> dates;
    private final int color;

    public EventDecorator(List<CalendarDay> dates, int color) {
        this.dates = new HashSet(dates);
        this.color = color;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new DotSpan(7F, color));
    }
}
