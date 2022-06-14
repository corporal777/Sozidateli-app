package com.example.ui.views.calendarView;

import androidx.annotation.NonNull;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;
import org.threeten.bp.temporal.ChronoUnit;
import org.threeten.bp.temporal.WeekFields;

public class WeekPagerAdapter extends CalendarPagerAdapter<WeekView> {

    public WeekPagerAdapter(MaterialCalendarView mcv) {
        super(mcv);
    }

    @Override
    protected WeekView createView(int position) {
        return new WeekView(mcv, getItem(position), mcv.getFirstDayOfWeek(), showWeekDays);
    }

    @Override
    protected int indexOf(WeekView view) {
        CalendarDay week = view.getFirstViewDay();
        return getRangeIndex().indexOf(week);
    }

    @Override
    protected boolean isInstanceOfView(Object object) {
        return object instanceof WeekView;
    }

    @Override
    protected DateRangeIndex createRangeIndex(CalendarDay min, CalendarDay max) {
        return new Weekly(min, max, mcv.getFirstDayOfWeek());
    }

    public static class Weekly implements DateRangeIndex {

        private final CalendarDay min;

        private final int count;

        private final DayOfWeek firstDayOfWeek;

        public Weekly(
                @NonNull final CalendarDay min,
                @NonNull final CalendarDay max,
                final DayOfWeek firstDayOfWeek) {
            this.firstDayOfWeek = firstDayOfWeek;
            this.min = getFirstDayOfWeek(min);
            this.count = indexOf(max) + 1;
        }

        @Override public int getCount() {
            return count;
        }

        @Override public int indexOf(final CalendarDay day) {
            final WeekFields weekFields = WeekFields.of(firstDayOfWeek, 1);
            final LocalDate temp = day.getDate().with(weekFields.dayOfWeek(), 1L);
            return (int) ChronoUnit.WEEKS.between(min.getDate(), temp);
        }

        @Override public CalendarDay getItem(final int position) {
            return CalendarDay.from(min.getDate().plusWeeks(position));
        }

        private CalendarDay getFirstDayOfWeek(@NonNull final CalendarDay day) {
            final LocalDate temp = day.getDate().with(WeekFields.of(firstDayOfWeek, 1).dayOfWeek(), 1L);
            return CalendarDay.from(temp);
        }
    }
}
