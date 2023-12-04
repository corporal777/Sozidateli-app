package com.pagercalendar.calendar.decorators;

import android.graphics.Color;

import com.pagercalendar.calendar.CalendarDay;
import com.pagercalendar.calendar.DayViewDecorator;
import com.pagercalendar.calendar.DayViewFacade;
import com.pagercalendar.calendar.spans.DotSpan;

public class SelectedDayDecorator implements DayViewDecorator {

    private CalendarDay date;

    public SelectedDayDecorator(CalendarDay date) {
        this.date = date;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return date == day;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new DotSpan(7F, Color.WHITE));
    }
}
