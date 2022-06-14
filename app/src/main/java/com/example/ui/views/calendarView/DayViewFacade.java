package com.example.ui.views.calendarView;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class DayViewFacade {

    private boolean isDecorated;

    private Drawable backgroundDrawable = null;
    private Drawable selectionDrawable = null;
    private final LinkedList<Span> spans = new LinkedList<>();
    private boolean daysDisabled = false;

    DayViewFacade() {
        isDecorated = false;
    }


    public void setBackgroundDrawable(@NonNull Drawable drawable) {
        if (drawable == null) {
            throw new IllegalArgumentException("Cannot be null");
        }
        this.backgroundDrawable = drawable;
        isDecorated = true;
    }

    public void setSelectionDrawable(@NonNull Drawable drawable) {
        if (drawable == null) {
            throw new IllegalArgumentException("Cannot be null");
        }
        selectionDrawable = drawable;
        isDecorated = true;
    }

    public void addSpan(@NonNull Object span) {
        if (spans != null) {
            this.spans.add(new Span(span));
            isDecorated = true;
        }
    }

    public void setDaysDisabled(boolean daysDisabled) {
        this.daysDisabled = daysDisabled;
        this.isDecorated = true;
    }

    void reset() {
        backgroundDrawable = null;
        selectionDrawable = null;
        spans.clear();
        isDecorated = false;
        daysDisabled = false;
    }

    void applyTo(DayViewFacade other) {
        if (selectionDrawable != null) {
            other.setSelectionDrawable(selectionDrawable);
        }
        if (backgroundDrawable != null) {
            other.setBackgroundDrawable(backgroundDrawable);
        }
        other.spans.addAll(spans);
        other.isDecorated |= this.isDecorated;
        other.daysDisabled = daysDisabled;
    }

    boolean isDecorated() {
        return isDecorated;
    }

    Drawable getSelectionDrawable() {
        return selectionDrawable;
    }

    Drawable getBackgroundDrawable() {
        return backgroundDrawable;
    }

    List<Span> getSpans() {
        return Collections.unmodifiableList(spans);
    }

    public boolean areDaysDisabled() {
        return daysDisabled;
    }

    static class Span {

        final Object span;

        public Span(Object span) {
            this.span = span;
        }
    }
}
