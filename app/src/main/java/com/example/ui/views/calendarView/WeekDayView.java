package com.example.ui.views.calendarView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatTextView;

import com.example.R;
import com.example.ui.views.calendarView.format.WeekDayFormatter;

import org.threeten.bp.DayOfWeek;

@SuppressLint("ViewConstructor")
class WeekDayView extends AppCompatTextView {

    private WeekDayFormatter formatter = WeekDayFormatter.DEFAULT;
    private DayOfWeek dayOfWeek;

    public WeekDayView(final Context context, final DayOfWeek dayOfWeek) {
        super(context);

        setGravity(Gravity.CENTER);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            setTextAlignment(TEXT_ALIGNMENT_CENTER);
        }
        setDaysTextAppearance(dayOfWeek);
        setDayOfWeek(dayOfWeek);
    }

    public void setWeekDayFormatter(@Nullable final WeekDayFormatter formatter) {
        this.formatter = formatter == null ? WeekDayFormatter.DEFAULT : formatter;
        setDayOfWeek(dayOfWeek);
    }

    private void setDaysTextAppearance(final DayOfWeek dayOfWeek){
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            setTextAppearance(getContext(), R.style.TextAppearance_MaterialCalendarWidget_WeekDayWeekend);
        } else {
            setTextAppearance(getContext(), R.style.TextAppearance_MaterialCalendarWidget_WeekDay);
        }
    }

    public void setDayOfWeek(final DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
        setText(formatter.format(dayOfWeek));
    }
}
