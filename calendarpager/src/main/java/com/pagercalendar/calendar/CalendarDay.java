package com.pagercalendar.calendar;

import android.os.Parcel;
import android.os.Parcelable;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.threeten.bp.LocalDate;

import java.util.Calendar;


public final class CalendarDay implements Parcelable {

    @NonNull
    private final LocalDate date;

    public CalendarDay(final int year, final int month, final int day) {
        date = LocalDate.of(year, month, day);
    }

    private CalendarDay(@NonNull final LocalDate date) {
        this.date = date;
    }

    @NonNull
    public static CalendarDay today() {
        return from(LocalDate.now());
    }

    @NonNull
    public static CalendarDay from(int year, int month, int day) {
        return new CalendarDay(year, month, day);
    }

    public static CalendarDay from(@Nullable LocalDate date) {
        if (date == null) {
            return null;
        }
        return new CalendarDay(date);
    }

    public int getYear() {
        return date.getYear();
    }

    public int getMonth() {
        return date.getMonthValue();
    }

    public int getDay() {
        return date.getDayOfMonth();
    }

    @NonNull
    public LocalDate getDate() {
        return date;
    }

    public static CalendarDay createCalendarDay(@Nullable Calendar calendar) {
        if (calendar == null) return CalendarDay.today();
        else {
            return new CalendarDay(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
        }
    }

    @NonNull
    public CalendarDay getMinDate() {
        return new CalendarDay(getYear(), getMonth(), 1);
    }

    @NonNull
    public CalendarDay getMaxDate() {
        return new CalendarDay(getYear(), getMonth(), getDate().getMonth().length(getDate().isLeapYear()));
    }


    public boolean isInRange(@Nullable CalendarDay minDate, @Nullable CalendarDay maxDate) {
        return !(minDate != null && minDate.isAfter(this)) &&
                !(maxDate != null && maxDate.isBefore(this));
    }

    public boolean isBefore(@NonNull final CalendarDay other) {
        return date.isBefore(other.getDate());
    }


    public boolean isAfter(@NonNull final CalendarDay other) {
        return date.isAfter(other.getDate());
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof CalendarDay && date.equals(((CalendarDay) o).getDate());
    }

    @Override
    public int hashCode() {
        return hashCode(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }

    private static int hashCode(int year, int month, int day) {
        //Should produce hashes like "20150401"
        return (year * 10000) + (month * 100) + day;
    }

    @Override
    public String toString() {
        return "CalendarDay{" + date.getYear() + "-" + date.getMonthValue() + "-"
                + date.getDayOfMonth() + "}";
    }

    @NonNull
    public String getDateString() {
        String month;
        String day;
        if (date.getMonthValue() < 10) month = "0" + date.getMonthValue();
        else month = String.valueOf(date.getMonthValue());

        if (date.getDayOfMonth() < 10) day = "0" + date.getDayOfMonth();
        else day = String.valueOf(date.getDayOfMonth());

        return date.getYear() + "-" + month + "-" + day;
    }


    public CalendarDay(Parcel in) {
        this(in.readInt(), in.readInt(), in.readInt());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(date.getYear());
        dest.writeInt(date.getMonthValue());
        dest.writeInt(date.getDayOfMonth());
    }

    public static final Creator<CalendarDay> CREATOR = new Creator<CalendarDay>() {
        public CalendarDay createFromParcel(Parcel in) {
            return new CalendarDay(in);
        }

        public CalendarDay[] newArray(int size) {
            return new CalendarDay[size];
        }
    };
}
