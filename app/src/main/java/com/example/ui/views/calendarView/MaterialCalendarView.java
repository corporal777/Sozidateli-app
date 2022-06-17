package com.example.ui.views.calendarView;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ArrayRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.example.R;
import com.example.ui.views.calendarView.format.ArrayWeekDayFormatter;
import com.example.ui.views.calendarView.format.DayFormatter;
import com.example.ui.views.calendarView.format.MonthArrayTitleFormatter;
import com.example.ui.views.calendarView.format.TitleFormatter;
import com.example.ui.views.calendarView.format.WeekDayFormatter;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;
import org.threeten.bp.temporal.WeekFields;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class MaterialCalendarView extends ViewGroup {

    public static final int INVALID_TILE_DIMENSION = -10;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(
            { SELECTION_MODE_NONE, SELECTION_MODE_SINGLE, SELECTION_MODE_MULTIPLE, SELECTION_MODE_RANGE })
    public @interface SelectionMode {
    }

    public static final int SELECTION_MODE_NONE = 0;

    public static final int SELECTION_MODE_SINGLE = 1;

    public static final int SELECTION_MODE_MULTIPLE = 2;

    public static final int SELECTION_MODE_RANGE = 3;

    @SuppressLint("UniqueConstants")
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(flag = true, value = {
            SHOW_NONE, SHOW_ALL, SHOW_DEFAULTS,
            SHOW_OUT_OF_RANGE, SHOW_OTHER_MONTHS, SHOW_DECORATED_DISABLED
    })
    public @interface ShowOtherDates {
    }

    public static final int SHOW_NONE = 0;

    public static final int SHOW_OTHER_MONTHS = 1;

    public static final int SHOW_OUT_OF_RANGE = 1 << 1;

    public static final int SHOW_DECORATED_DISABLED = 1 << 2;

    public static final int SHOW_DEFAULTS = SHOW_DECORATED_DISABLED;

    public static final int SHOW_ALL =
            SHOW_OTHER_MONTHS | SHOW_OUT_OF_RANGE | SHOW_DECORATED_DISABLED;

    public static final int VERTICAL = 0;

    public static final int HORIZONTAL = 1;

    public static final int DEFAULT_TILE_SIZE_DP = 44;
    private static final int DEFAULT_DAYS_IN_WEEK = 7;
    private static final int DEFAULT_MAX_WEEKS = 6;
    private static final int DAY_NAMES_ROW = 1;

    private final TitleChanger titleChanger;

    private final TextView title;
    private final ImageView buttonPast;
    private final ImageView buttonFuture;
    private final CalendarPager pager;
    private CalendarPagerAdapter<?> adapter;
    private CalendarDay currentMonth;
    private LinearLayout topbar;
    private CalendarMode calendarMode;
    private boolean mDynamicHeightEnabled;

    private final ArrayList<DayViewDecorator> dayViewDecorators = new ArrayList<>();

    private final OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == buttonFuture) {
                pager.setCurrentItem(pager.getCurrentItem() + 1, true);
            } else if (v == buttonPast) {
                pager.setCurrentItem(pager.getCurrentItem() - 1, true);
            }
        }
    };

    private final ViewPager.OnPageChangeListener pageChangeListener =
            new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    titleChanger.setPreviousMonth(currentMonth);
                    currentMonth = adapter.getItem(position);
                    updateUi();

                    dispatchOnMonthChanged(currentMonth);
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }

                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }
            };

    private CalendarDay minDate = null;
    private CalendarDay maxDate = null;

    private OnDateSelectedListener listener;
    private OnDateLongClickListener longClickListener;
    private OnMonthChangedListener monthListener;
    private OnRangeSelectedListener rangeListener;

    CharSequence calendarContentDescription;
    private int accentColor = 0;
    private int tileHeight = INVALID_TILE_DIMENSION;
    private int tileWidth = INVALID_TILE_DIMENSION;
    @SelectionMode
    private int selectionMode = SELECTION_MODE_SINGLE;
    private boolean allowClickDaysOutsideCurrentMonth = true;
    private DayOfWeek firstDayOfWeek;
    private boolean showWeekDays;

    private State state;

    public MaterialCalendarView(Context context) {
        this(context, null);
    }

    public MaterialCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //If we're on good Android versions, turn off clipping for cool effects
            setClipToPadding(false);
            setClipChildren(false);
        } else {
            //Old Android does not like _not_ clipping view pagers, we need to clip
            setClipChildren(true);
            setClipToPadding(true);
        }

        final LayoutInflater inflater =
                (LayoutInflater) getContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        final View content = inflater.inflate(R.layout.calendar_view, null, false);

        topbar = content.findViewById(R.id.header);
        buttonPast = content.findViewById(R.id.previous);
        title = content.findViewById(R.id.month_name);
        buttonFuture = content.findViewById(R.id.next);
        pager = new CalendarPager(getContext());

        buttonPast.setOnClickListener(onClickListener);
        buttonFuture.setOnClickListener(onClickListener);

        titleChanger = new TitleChanger(title);

        pager.setOnPageChangeListener(pageChangeListener);
        pager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                position = (float) Math.sqrt(1 - Math.abs(position));
                page.setAlpha(position);
            }
        });

        TypedArray a = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.MaterialCalendarView, 0, 0);
        try {
            int calendarModeIndex = a.getInteger(
                    R.styleable.MaterialCalendarView_mcv_calendarMode,
                    0
            );
            int firstDayOfWeekInt = a.getInteger(
                    R.styleable.MaterialCalendarView_mcv_firstDayOfWeek,
                    -1
            );

            titleChanger.setOrientation(
                    a.getInteger(
                            R.styleable.MaterialCalendarView_mcv_titleAnimationOrientation,
                            VERTICAL
                    ));

            if (firstDayOfWeekInt >= 1 && firstDayOfWeekInt <= 7) {
                firstDayOfWeek = DayOfWeek.of(firstDayOfWeekInt);
            } else {
                firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
            }

            showWeekDays = a.getBoolean(R.styleable.MaterialCalendarView_mcv_showWeekDays, true);

            newState()
                    .setFirstDayOfWeek(firstDayOfWeek)
                    .setCalendarDisplayMode(CalendarMode.values()[calendarModeIndex])
                    .setShowWeekDays(showWeekDays)
                    .commit();

            setSelectionMode(a.getInteger(
                    R.styleable.MaterialCalendarView_mcv_selectionMode,
                    SELECTION_MODE_SINGLE
            ));

            final int tileSize = a.getLayoutDimension(
                    R.styleable.MaterialCalendarView_mcv_tileSize,
                    INVALID_TILE_DIMENSION
            );
            if (tileSize > INVALID_TILE_DIMENSION) {
                setTileSize(tileSize);
            }

            final int tileWidth = a.getLayoutDimension(
                    R.styleable.MaterialCalendarView_mcv_tileWidth,
                    INVALID_TILE_DIMENSION
            );
            if (tileWidth > INVALID_TILE_DIMENSION) {
                setTileWidth(tileWidth);
            }

            final int tileHeight = a.getLayoutDimension(
                    R.styleable.MaterialCalendarView_mcv_tileHeight,
                    INVALID_TILE_DIMENSION
            );
            if (tileHeight > INVALID_TILE_DIMENSION) {
                setTileHeight(tileHeight);
            }

            setLeftArrow(
                    a.getResourceId(
                            R.styleable.MaterialCalendarView_mcv_leftArrow,
                            R.drawable.ic_calendar_left_arrow
                    )
            );
            setRightArrow(
                    a.getResourceId(
                            R.styleable.MaterialCalendarView_mcv_rightArrow,
                            R.drawable.ic_calendar_right_arrow
                    )
            );

            setSelectionColor(
                    a.getColor(
                            R.styleable.MaterialCalendarView_mcv_selectionColor,
                            getThemeAccentColor(context)
                    )
            );

            CharSequence[] array = a.getTextArray(R.styleable.MaterialCalendarView_mcv_weekDayLabels);
            if (array != null) {
                setWeekDayFormatter(new ArrayWeekDayFormatter(array));
            }

            array = a.getTextArray(R.styleable.MaterialCalendarView_mcv_monthLabels);
            if (array != null) {
                setTitleFormatter(new MonthArrayTitleFormatter(array));
            }

            setHeaderTextAppearance(a.getResourceId(
                    R.styleable.MaterialCalendarView_mcv_headerTextAppearance,
                    R.style.TextAppearance_MaterialCalendarWidget_Header
            ));
//            setWeekDayTextAppearance(a.getResourceId(
//                    R.styleable.MaterialCalendarView_mcv_weekDayTextAppearance,
//                    R.style.TextAppearance_MaterialCalendarWidget_WeekDay
//            ));
//            setDateTextAppearance(a.getResourceId(
//                    R.styleable.MaterialCalendarView_mcv_dateTextAppearance,
//                    R.style.TextAppearance_MaterialCalendarWidget_Date
//            ));
            //noinspection ResourceType
            setShowOtherDates(a.getInteger(
                    R.styleable.MaterialCalendarView_mcv_showOtherDates,
                    SHOW_DEFAULTS
            ));

            setAllowClickDaysOutsideCurrentMonth(a.getBoolean(
                    R.styleable.MaterialCalendarView_mcv_allowClickDaysOutsideCurrentMonth,
                    true
            ));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            a.recycle();
        }

        // Adapter is created while parsing the TypedArray attrs, so setup has to happen after
        setupChildren();

        currentMonth = CalendarDay.today();
        setCurrentDate(currentMonth);

        if (isInEditMode()) {
            removeView(pager);
            MonthView monthView = new MonthView(this, currentMonth, getFirstDayOfWeek(), true);
            monthView.setSelectionColor(getSelectionColor());
            monthView.setDateTextAppearance(adapter.getDateTextAppearance());
            monthView.setWeekDayTextAppearance(adapter.getWeekDayTextAppearance());
            monthView.setShowOtherDates(getShowOtherDates());
            addView(monthView, new LayoutParams(calendarMode.visibleWeeksCount + DAY_NAMES_ROW));
        }
    }

    private void setupChildren() {
        addView(topbar);

        pager.setId(R.id.mcv_pager);
        pager.setOffscreenPageLimit(1);

        int tileHeight = showWeekDays ? calendarMode.visibleWeeksCount + DAY_NAMES_ROW
                : calendarMode.visibleWeeksCount;
        addView(pager, new LayoutParams(tileHeight));
    }

    private void updateUi() {
        titleChanger.change(currentMonth);
        enableView(buttonPast, canGoBack());
        enableView(buttonFuture, canGoForward());
    }

    public void setSelectionMode(final @SelectionMode int mode) {
        final @SelectionMode int oldMode = this.selectionMode;
        this.selectionMode = mode;
        switch (mode) {
            case SELECTION_MODE_RANGE:
                clearSelection();
                break;
            case SELECTION_MODE_MULTIPLE:
                break;
            case SELECTION_MODE_SINGLE:
                if (oldMode == SELECTION_MODE_MULTIPLE || oldMode == SELECTION_MODE_RANGE) {
                    //We should only have one selection now, so we should pick one
                    List<CalendarDay> dates = getSelectedDates();
                    if (!dates.isEmpty()) {
                        setSelectedDate(getSelectedDate());
                    }
                }
                break;
            default:
            case SELECTION_MODE_NONE:
                this.selectionMode = SELECTION_MODE_NONE;
                if (oldMode != SELECTION_MODE_NONE) {
                    //No selection! Clear out!
                    clearSelection();
                }
                break;
        }

        adapter.setSelectionEnabled(selectionMode != SELECTION_MODE_NONE);
    }

    public void goToPrevious() {
        if (canGoBack()) {
            pager.setCurrentItem(pager.getCurrentItem() - 1, true);
        }
    }

    public void goToNext() {
        if (canGoForward()) {
            pager.setCurrentItem(pager.getCurrentItem() + 1, true);
        }
    }

    @SelectionMode
    public int getSelectionMode() {
        return selectionMode;
    }

    @Deprecated
    public int getTileSize() {
        return Math.max(tileHeight, tileWidth);
    }

    public void setTileSize(int size) {
        this.tileWidth = size;
        this.tileHeight = size;
        requestLayout();
    }

    public void setTileSizeDp(int tileSizeDp) {
        setTileSize(dpToPx(tileSizeDp));
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public void setTileHeight(int height) {
        this.tileHeight = height;
        requestLayout();
    }

    public void setTileHeightDp(int tileHeightDp) {
        setTileHeight(dpToPx(tileHeightDp));
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public void setTileWidth(int width) {
        this.tileWidth = width;
        requestLayout();
    }

    public void setTileWidthDp(int tileWidthDp) {
        setTileWidth(dpToPx(tileWidthDp));
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()
        );
    }

    public boolean canGoForward() {
        return pager.getCurrentItem() < (adapter.getCount() - 1);
    }

    public boolean canGoBack() {
        return pager.getCurrentItem() > 0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return pager.dispatchTouchEvent(event);
    }

    public int getSelectionColor() {
        return accentColor;
    }

    public void setSelectionColor(int color) {
        if (color == 0) {
            if (!isInEditMode()) {
                return;
            } else {
                color = Color.BLACK;
            }
        }
        accentColor = color;
        adapter.setSelectionColor(color);
        invalidate();
    }

    public void setContentDescriptionArrowPast(final CharSequence description) {
        buttonPast.setContentDescription(description);
    }

    public void setContentDescriptionArrowFuture(final CharSequence description) {
        buttonFuture.setContentDescription(description);
    }

    public void setContentDescriptionCalendar(final CharSequence description) {
        calendarContentDescription = description;
    }

//    public CharSequence getCalendarContentDescription() {
//        return calendarContentDescription != null
//                ? calendarContentDescription
//                : getContext().getString(R.string.calendar);
//    }

    public void setDayFormatterContentDescription(DayFormatter formatter) {
        adapter.setDayFormatterContentDescription(formatter);
    }

    public Drawable getLeftArrow() {
        return buttonPast.getDrawable();
    }

    public void setLeftArrow(@DrawableRes final int icon) {
        buttonPast.setImageResource(icon);
    }

    public Drawable getRightArrow() {
        return buttonFuture.getDrawable();
    }

    public void setRightArrow(@DrawableRes final int icon) {
        buttonFuture.setImageResource(icon);
    }

    public void setHeaderTextAppearance(int resourceId) {
        title.setTextAppearance(getContext(), resourceId);
    }

    public void setDateTextAppearance(int resourceId) {
        adapter.setDateTextAppearance(resourceId);
    }

    public void setWeekDayTextAppearance(int resourceId) {
        adapter.setWeekDayTextAppearance(resourceId);
    }

    @Nullable public CalendarDay getSelectedDate() {
        List<CalendarDay> dates = adapter.getSelectedDates();
        if (dates.isEmpty()) {
            return null;
        } else {
            return dates.get(dates.size() - 1);
        }
    }

    @NonNull public List<CalendarDay> getSelectedDates() {
        return adapter.getSelectedDates();
    }

    public void clearSelection() {
        List<CalendarDay> dates = getSelectedDates();
        adapter.clearSelections();
        for (CalendarDay day : dates) {
            dispatchOnDateSelected(day, false);
        }
    }

    public void setSelectedDate(@Nullable LocalDate date) {
        setSelectedDate(CalendarDay.from(date));
    }

    public void setSelectedDate(@Nullable CalendarDay date) {
        clearSelection();
        if (date != null) {
            setDateSelected(date, true);
        }
    }

    public void setDateSelected(@Nullable CalendarDay day, boolean selected) {
        if (day == null) {
            return;
        }
        adapter.setDateSelected(day, selected);
    }

    public CalendarDay getCurrentDate() {
        return adapter.getItem(pager.getCurrentItem());
    }

    public void setCurrentDate(@Nullable LocalDate calendar) {
        setCurrentDate(CalendarDay.from(calendar));
    }

    public void setCurrentDate(@Nullable CalendarDay day) {
        setCurrentDate(day, true);
    }

    public void setCurrentDate(@Nullable CalendarDay day, boolean useSmoothScroll) {
        if (day == null) {
            return;
        }
        int index = adapter.getIndexForDay(day);
        pager.setCurrentItem(index, useSmoothScroll);
        updateUi();
    }

    public CalendarDay getMinimumDate() {
        return minDate;
    }

    public CalendarDay getMaximumDate() {
        return maxDate;
    }

    public void setShowOtherDates(@ShowOtherDates int showOtherDates) {
        adapter.setShowOtherDates(showOtherDates);
    }

    public void setAllowClickDaysOutsideCurrentMonth(final boolean enabled) {
        this.allowClickDaysOutsideCurrentMonth = enabled;
    }

    public void setWeekDayFormatter(WeekDayFormatter formatter) {
        adapter.setWeekDayFormatter(formatter == null ? WeekDayFormatter.DEFAULT : formatter);
    }

    public void setDayFormatter(DayFormatter formatter) {
        adapter.setDayFormatter(formatter == null ? DayFormatter.DEFAULT : formatter);
    }

    public void setWeekDayLabels(CharSequence[] weekDayLabels) {
        setWeekDayFormatter(new ArrayWeekDayFormatter(weekDayLabels));
    }

    public void setWeekDayLabels(@ArrayRes int arrayRes) {
        setWeekDayLabels(getResources().getTextArray(arrayRes));
    }

    @ShowOtherDates
    public int getShowOtherDates() {
        return adapter.getShowOtherDates();
    }

    public boolean allowClickDaysOutsideCurrentMonth() {
        return allowClickDaysOutsideCurrentMonth;
    }

    public boolean isShowWeekDays() {
        return showWeekDays;
    }

    public void setTitleFormatter(@Nullable TitleFormatter titleFormatter) {
        titleChanger.setTitleFormatter(titleFormatter);
        adapter.setTitleFormatter(titleFormatter);
        updateUi();
    }


    public void setTitleMonths(CharSequence[] monthLabels) {
        setTitleFormatter(new MonthArrayTitleFormatter(monthLabels));
    }

    public void setTitleMonths(@ArrayRes int arrayRes) {
        setTitleMonths(getResources().getTextArray(arrayRes));
    }

    public void setTitleAnimationOrientation(final int orientation) {
        titleChanger.setOrientation(orientation);
    }

    public int getTitleAnimationOrientation() {
        return titleChanger.getOrientation();
    }

    public void setTopbarVisible(boolean visible) {
        topbar.setVisibility(visible ? View.VISIBLE : View.GONE);
        requestLayout();
    }

    public boolean getTopbarVisible() {
        return topbar.getVisibility() == View.VISIBLE;
    }
    public CalendarMode getCalendarMode() {
        return calendarMode;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        SavedState ss = new SavedState(super.onSaveInstanceState());
        ss.showOtherDates = getShowOtherDates();
        ss.allowClickDaysOutsideCurrentMonth = allowClickDaysOutsideCurrentMonth();
        ss.minDate = getMinimumDate();
        ss.maxDate = getMaximumDate();
        ss.selectedDates = getSelectedDates();
        ss.selectionMode = getSelectionMode();
        ss.topbarVisible = getTopbarVisible();
        ss.dynamicHeightEnabled = mDynamicHeightEnabled;
        ss.currentMonth = currentMonth;
        ss.cacheCurrentPosition = state.cacheCurrentPosition;
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        state().edit()
                .setMinimumDate(ss.minDate)
                .setMaximumDate(ss.maxDate)
                .isCacheCalendarPositionEnabled(ss.cacheCurrentPosition)
                .commit();

        setShowOtherDates(ss.showOtherDates);
        setAllowClickDaysOutsideCurrentMonth(ss.allowClickDaysOutsideCurrentMonth);
        clearSelection();
        for (CalendarDay calendarDay : ss.selectedDates) {
            setDateSelected(calendarDay, true);
        }
        setTopbarVisible(ss.topbarVisible);
        setSelectionMode(ss.selectionMode);
        setDynamicHeightEnabled(ss.dynamicHeightEnabled);
        setCurrentDate(ss.currentMonth);
    }

    @Override
    protected void dispatchSaveInstanceState(@NonNull SparseArray<Parcelable> container) {
        dispatchFreezeSelfOnly(container);
    }

    @Override
    protected void dispatchRestoreInstanceState(@NonNull SparseArray<Parcelable> container) {
        dispatchThawSelfOnly(container);
    }

    private void setRangeDates(CalendarDay min, CalendarDay max) {
        CalendarDay c = currentMonth;
        adapter.setRangeDates(min, max);
        currentMonth = c;
        if (min != null) {
            currentMonth = min.isAfter(currentMonth) ? min : currentMonth;
        }
        int position = adapter.getIndexForDay(c);
        pager.setCurrentItem(position, false);
        updateUi();
    }

    public static class SavedState extends BaseSavedState {

        int showOtherDates = SHOW_DEFAULTS;
        boolean allowClickDaysOutsideCurrentMonth = true;
        CalendarDay minDate = null;
        CalendarDay maxDate = null;
        List<CalendarDay> selectedDates = new ArrayList<>();
        boolean topbarVisible = true;
        int selectionMode = SELECTION_MODE_SINGLE;
        boolean dynamicHeightEnabled = false;
        CalendarDay currentMonth = null;
        boolean cacheCurrentPosition;

        SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(@NonNull Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(showOtherDates);
            out.writeByte((byte) (allowClickDaysOutsideCurrentMonth ? 1 : 0));
            out.writeParcelable(minDate, 0);
            out.writeParcelable(maxDate, 0);
            out.writeTypedList(selectedDates);
            out.writeInt(topbarVisible ? 1 : 0);
            out.writeInt(selectionMode);
            out.writeInt(dynamicHeightEnabled ? 1 : 0);
            out.writeParcelable(currentMonth, 0);
            out.writeByte((byte) (cacheCurrentPosition ? 1 : 0));
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        private SavedState(Parcel in) {
            super(in);
            showOtherDates = in.readInt();
            allowClickDaysOutsideCurrentMonth = in.readByte() != 0;
            ClassLoader loader = CalendarDay.class.getClassLoader();
            minDate = in.readParcelable(loader);
            maxDate = in.readParcelable(loader);
            in.readTypedList(selectedDates, CalendarDay.CREATOR);
            topbarVisible = in.readInt() == 1;
            selectionMode = in.readInt();
            dynamicHeightEnabled = in.readInt() == 1;
            currentMonth = in.readParcelable(loader);
            cacheCurrentPosition = in.readByte() != 0;
        }
    }

    private static int getThemeAccentColor(Context context) {
        int colorAttr;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            colorAttr = android.R.attr.colorAccent;
        } else {
            //Get colorAccent defined for AppCompat
            colorAttr =
                    context.getResources().getIdentifier("colorAccent", "attr", context.getPackageName());
        }
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(colorAttr, outValue, true);
        return outValue.data;
    }

    public DayOfWeek getFirstDayOfWeek() {
        return firstDayOfWeek;
    }

    public void setDynamicHeightEnabled(boolean useDynamicHeight) {
        this.mDynamicHeightEnabled = useDynamicHeight;
    }

    public boolean isDynamicHeightEnabled() {
        return mDynamicHeightEnabled;
    }

    public void addDecorators(Collection<? extends DayViewDecorator> decorators) {
        if (decorators == null) {
            return;
        }

        dayViewDecorators.addAll(decorators);
        adapter.setDecorators(dayViewDecorators);
    }

    public void addDecorators(DayViewDecorator... decorators) {
        addDecorators(Arrays.asList(decorators));
    }

    public void addDecorator(DayViewDecorator decorator) {
        if (decorator == null) {
            return;
        }
        dayViewDecorators.add(decorator);
        adapter.setDecorators(dayViewDecorators);
    }

    public void removeDecorators() {
        dayViewDecorators.clear();
        adapter.setDecorators(dayViewDecorators);
    }

    public void removeDecorator(DayViewDecorator decorator) {
        dayViewDecorators.remove(decorator);
        adapter.setDecorators(dayViewDecorators);
    }

    public void invalidateDecorators() {
        adapter.invalidateDecorators();
    }

    public void setOnDateChangedListener(OnDateSelectedListener listener) {
        this.listener = listener;
    }

    public void setOnDateLongClickListener(OnDateLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    public void setOnMonthChangedListener(OnMonthChangedListener listener) {
        this.monthListener = listener;
    }

    public void setOnRangeSelectedListener(OnRangeSelectedListener listener) {
        this.rangeListener = listener;
    }

    public void setOnTitleClickListener(final OnClickListener listener) {
        title.setOnClickListener(listener);
    }

    protected void dispatchOnDateSelected(final CalendarDay day, final boolean selected) {
        if (listener != null) {
            listener.onDateSelected(MaterialCalendarView.this, day, selected);
        }
    }

    protected void dispatchOnRangeSelected(@NonNull final List<CalendarDay> days) {
        if (rangeListener != null) {
            rangeListener.onRangeSelected(MaterialCalendarView.this, days);
        }
    }

    protected void dispatchOnMonthChanged(final CalendarDay day) {
        if (monthListener != null) {
            monthListener.onMonthChanged(MaterialCalendarView.this, day);
        }
    }

    protected void onDateClicked(@NonNull CalendarDay date, boolean nowSelected) {
        switch (selectionMode) {
            case SELECTION_MODE_MULTIPLE: {
                adapter.setDateSelected(date, nowSelected);
                dispatchOnDateSelected(date, nowSelected);
            }
            break;
            case SELECTION_MODE_RANGE: {
                final List<CalendarDay> currentSelection = adapter.getSelectedDates();

                if (currentSelection.size() == 0) {
                    // Selecting the first date of a range
                    adapter.setDateSelected(date, nowSelected);
                    dispatchOnDateSelected(date, nowSelected);
                } else if (currentSelection.size() == 1) {
                    // Selecting the second date of a range
                    final CalendarDay firstDaySelected = currentSelection.get(0);
                    if (firstDaySelected.equals(date)) {
                        // Right now, we are not supporting a range of one day, so we are removing the day instead.
                        adapter.setDateSelected(date, nowSelected);
                        dispatchOnDateSelected(date, nowSelected);
                    } else if (firstDaySelected.isAfter(date)) {
                        // Selecting a range, dispatching in reverse order...
                        adapter.selectRange(date, firstDaySelected);
                        dispatchOnRangeSelected(adapter.getSelectedDates());
                    } else {
                        // Selecting a range, dispatching in order...
                        adapter.selectRange(firstDaySelected, date);
                        dispatchOnRangeSelected(adapter.getSelectedDates());
                    }
                } else {
                    // Clearing selection and making a selection of the new date.
                    adapter.clearSelections();
                    adapter.setDateSelected(date, nowSelected);
                    dispatchOnDateSelected(date, nowSelected);
                }
            }
            break;
            default:
            case SELECTION_MODE_SINGLE: {
                adapter.clearSelections();
                adapter.setDateSelected(date, true);
                dispatchOnDateSelected(date, true);
            }
            break;
        }
    }

    public void selectRange(final CalendarDay firstDay, final CalendarDay lastDay) {
        if (firstDay == null || lastDay == null) {
            return;
        } else if (firstDay.isAfter(lastDay)) {
            adapter.selectRange(lastDay, firstDay);
            dispatchOnRangeSelected(adapter.getSelectedDates());
        } else {
            adapter.selectRange(firstDay, lastDay);
            dispatchOnRangeSelected(adapter.getSelectedDates());
        }
    }

    protected void onDateClicked(final DayView dayView) {
        final CalendarDay currentDate = getCurrentDate();
        final CalendarDay selectedDate = dayView.getDate();
        final int currentMonth = currentDate.getMonth();
        final int selectedMonth = selectedDate.getMonth();

        if (calendarMode == CalendarMode.MONTHS
                && allowClickDaysOutsideCurrentMonth
                && currentMonth != selectedMonth) {
            if (currentDate.isAfter(selectedDate)) {
                goToPrevious();
            } else if (currentDate.isBefore(selectedDate)) {
                goToNext();
            }
        }
        onDateClicked(dayView.getDate(), !dayView.isChecked());
    }

    protected void onDateLongClicked(final DayView dayView) {
        if (longClickListener != null) {
            longClickListener.onDateLongClick(MaterialCalendarView.this, dayView.getDate());
        }
    }

    protected void onDateUnselected(CalendarDay date) {
        dispatchOnDateSelected(date, false);
    }

    public static boolean showOtherMonths(@ShowOtherDates int showOtherDates) {
        return (showOtherDates & SHOW_OTHER_MONTHS) != 0;
    }

    public static boolean showOutOfRange(@ShowOtherDates int showOtherDates) {
        return (showOtherDates & SHOW_OUT_OF_RANGE) != 0;
    }

    public static boolean showDecoratedDisabled(@ShowOtherDates int showOtherDates) {
        return (showOtherDates & SHOW_DECORATED_DISABLED) != 0;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(1);
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final int specWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int specWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int specHeightSize = MeasureSpec.getSize(heightMeasureSpec);
        final int specHeightMode = MeasureSpec.getMode(heightMeasureSpec);

        //We need to disregard padding for a while. This will be added back later
        final int desiredWidth = specWidthSize - getPaddingLeft() - getPaddingRight();
        final int desiredHeight = specHeightSize - getPaddingTop() - getPaddingBottom();

        final int weekCount = getWeekCountBasedOnMode();

        final int viewTileHeight = getTopbarVisible() ? (weekCount + 1) : weekCount;

        //Calculate independent tile sizes for later
        int desiredTileWidth = desiredWidth / DEFAULT_DAYS_IN_WEEK;
        int desiredTileHeight = desiredHeight / viewTileHeight;

        int measureTileSize = -1;
        int measureTileWidth = -1;
        int measureTileHeight = -1;

        if (this.tileWidth != INVALID_TILE_DIMENSION || this.tileHeight != INVALID_TILE_DIMENSION) {
            if (this.tileWidth > 0) {
                //We have a tileWidth set, we should use that
                measureTileWidth = this.tileWidth;
            } else {
                measureTileWidth = desiredTileWidth;
            }
            if (this.tileHeight > 0) {
                //We have a tileHeight set, we should use that
                measureTileHeight = this.tileHeight;
            } else {
                measureTileHeight = desiredTileHeight;
            }
        } else if (specWidthMode == MeasureSpec.EXACTLY || specWidthMode == MeasureSpec.AT_MOST) {
            if (specHeightMode == MeasureSpec.EXACTLY) {
                //Pick the smaller of the two explicit sizes
                measureTileSize = Math.min(desiredTileWidth, desiredTileHeight);
            } else {
                //Be the width size the user wants
                measureTileSize = desiredTileWidth;
            }
        } else if (specHeightMode == MeasureSpec.EXACTLY || specHeightMode == MeasureSpec.AT_MOST) {
            //Be the height size the user wants
            measureTileSize = desiredTileHeight;
        }

        if (measureTileSize > 0) {
            //Use measureTileSize if set
            measureTileHeight = measureTileSize;
            measureTileWidth = measureTileSize;
        } else if (measureTileSize <= 0) {
            if (measureTileWidth <= 0) {
                //Set width to default if no value were set
                measureTileWidth = dpToPx(DEFAULT_TILE_SIZE_DP);
            }
            if (measureTileHeight <= 0) {
                //Set height to default if no value were set
                measureTileHeight = dpToPx(DEFAULT_TILE_SIZE_DP);
            }
        }

        //Calculate our size based off our measured tile size
        int measuredWidth = measureTileWidth * DEFAULT_DAYS_IN_WEEK;
        int measuredHeight = measureTileHeight * viewTileHeight;

        //Put padding back in from when we took it away
        measuredWidth += getPaddingLeft() + getPaddingRight();
        measuredHeight += getPaddingTop() + getPaddingBottom();

        //Contract fulfilled, setting out measurements
        setMeasuredDimension(
                //We clamp inline because we want to use un-clamped versions on the children
                clampSize(measuredWidth, widthMeasureSpec),
                clampSize(measuredHeight, heightMeasureSpec)
        );

        int count = getChildCount();

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);

            LayoutParams p = (LayoutParams) child.getLayoutParams();

            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                    DEFAULT_DAYS_IN_WEEK * measureTileWidth,
                    MeasureSpec.EXACTLY
            );

            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                    p.height * measureTileHeight,
                    MeasureSpec.EXACTLY
            );

            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }
    }

    private int getWeekCountBasedOnMode() {
        int weekCount = calendarMode.visibleWeeksCount;
        final boolean isInMonthsMode = calendarMode.equals(CalendarMode.MONTHS);
        if (isInMonthsMode && mDynamicHeightEnabled && adapter != null && pager != null) {
            final LocalDate cal = adapter.getItem(pager.getCurrentItem()).getDate();
            final LocalDate tempLastDay = cal.withDayOfMonth(cal.lengthOfMonth());
            weekCount = tempLastDay.get(WeekFields.of(firstDayOfWeek, 1).weekOfMonth());
        }
        return showWeekDays ? weekCount + DAY_NAMES_ROW : weekCount;
    }

    private static int clampSize(int size, int spec) {
        int specMode = MeasureSpec.getMode(spec);
        int specSize = MeasureSpec.getSize(spec);
        switch (specMode) {
            case MeasureSpec.EXACTLY: {
                return specSize;
            }
            case MeasureSpec.AT_MOST: {
                return Math.min(size, specSize);
            }
            case MeasureSpec.UNSPECIFIED:
            default: {
                return size;
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int count = getChildCount();

        final int parentLeft = getPaddingLeft();
        final int parentWidth = right - left - parentLeft - getPaddingRight();

        int childTop = getPaddingTop();

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }

            final int width = child.getMeasuredWidth();
            final int height = child.getMeasuredHeight();

            int delta = (parentWidth - width) / 2;
            int childLeft = parentLeft + delta;

            child.layout(childLeft, childTop, childLeft + width, childTop + height);

            childTop += height;
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(1);
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(1);
    }

    @Override
    public void onInitializeAccessibilityEvent(@NonNull AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(MaterialCalendarView.class.getName());
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(@NonNull AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(MaterialCalendarView.class.getName());
    }

    protected static class LayoutParams extends MarginLayoutParams {
        public LayoutParams(int tileHeight) {
            super(MATCH_PARENT, tileHeight);
        }
    }

    public void setPagingEnabled(boolean pagingEnabled) {
        pager.setPagingEnabled(pagingEnabled);
        updateUi();
    }

    public boolean isPagingEnabled() {
        return pager.isPagingEnabled();
    }

    public State state() {
        return state;
    }

    public StateBuilder newState() {
        return new StateBuilder();
    }

    public class State {
        private final CalendarMode calendarMode;
        private final DayOfWeek firstDayOfWeek;
        private final CalendarDay minDate;
        private final CalendarDay maxDate;
        private final boolean cacheCurrentPosition;
        private final boolean showWeekDays;

        private State(final StateBuilder builder) {
            calendarMode = builder.calendarMode;
            firstDayOfWeek = builder.firstDayOfWeek;
            minDate = builder.minDate;
            maxDate = builder.maxDate;
            cacheCurrentPosition = builder.cacheCurrentPosition;
            showWeekDays = builder.showWeekDays;
        }

        public StateBuilder edit() {
            return new StateBuilder(this);
        }
    }

    public class StateBuilder {
        private CalendarMode calendarMode;
        private DayOfWeek firstDayOfWeek;
        private boolean cacheCurrentPosition = false;
        private CalendarDay minDate = null;
        private CalendarDay maxDate = null;
        private boolean showWeekDays;

        public StateBuilder() {
            calendarMode = CalendarMode.MONTHS;
            firstDayOfWeek =
                    LocalDate.now().with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1).getDayOfWeek();
        }

        private StateBuilder(final State state) {
            calendarMode = state.calendarMode;
            firstDayOfWeek = state.firstDayOfWeek;
            minDate = state.minDate;
            maxDate = state.maxDate;
            cacheCurrentPosition = state.cacheCurrentPosition;
            showWeekDays = state.showWeekDays;
        }

        public StateBuilder setFirstDayOfWeek(DayOfWeek day) {
            this.firstDayOfWeek = day;
            return this;
        }

        public StateBuilder setCalendarDisplayMode(CalendarMode mode) {
            this.calendarMode = mode;
            return this;
        }

        public StateBuilder setMinimumDate(@Nullable LocalDate date) {
            setMinimumDate(CalendarDay.from(date));
            return this;
        }

        public StateBuilder setMinimumDate(@Nullable CalendarDay calendar) {
            minDate = calendar;
            return this;
        }

        public StateBuilder setMaximumDate(@Nullable LocalDate date) {
            setMaximumDate(CalendarDay.from(date));
            return this;
        }

        public StateBuilder setMaximumDate(@Nullable CalendarDay calendar) {
            maxDate = calendar;
            return this;
        }

        public StateBuilder setShowWeekDays(boolean showWeekDays) {
            this.showWeekDays = showWeekDays;
            return this;
        }

        public StateBuilder isCacheCalendarPositionEnabled(final boolean cacheCurrentPosition) {
            this.cacheCurrentPosition = cacheCurrentPosition;
            return this;
        }

        public void commit() {
            MaterialCalendarView.this.commit(new State(this));
        }
    }

    private void commit(State state) {
        // Use the calendarDayToShow to determine which date to focus on for the case of switching between month and week views
        CalendarDay calendarDayToShow = null;
        if (adapter != null && state.cacheCurrentPosition) {
            calendarDayToShow = adapter.getItem(pager.getCurrentItem());
            if (calendarMode != state.calendarMode) {
                CalendarDay currentlySelectedDate = getSelectedDate();
                if (calendarMode == CalendarMode.MONTHS && currentlySelectedDate != null) {
                    // Going from months to weeks
                    LocalDate lastVisibleCalendar = calendarDayToShow.getDate();
                    CalendarDay lastVisibleCalendarDay = CalendarDay.from(lastVisibleCalendar.plusDays(1));
                    if (currentlySelectedDate.equals(calendarDayToShow) ||
                            (currentlySelectedDate.isAfter(calendarDayToShow) && currentlySelectedDate.isBefore(
                                    lastVisibleCalendarDay))) {
                        // Currently selected date is within view, so center on that
                        calendarDayToShow = currentlySelectedDate;
                    }
                } else if (calendarMode == CalendarMode.WEEKS) {
                    // Going from weeks to months
                    LocalDate lastVisibleCalendar = calendarDayToShow.getDate();
                    CalendarDay lastVisibleCalendarDay = CalendarDay.from(lastVisibleCalendar.plusDays(6));
                    if (currentlySelectedDate != null &&
                            (currentlySelectedDate.equals(calendarDayToShow) || currentlySelectedDate.equals(
                                    lastVisibleCalendarDay) ||
                                    (currentlySelectedDate.isAfter(calendarDayToShow)
                                            && currentlySelectedDate.isBefore(lastVisibleCalendarDay)))) {
                        // Currently selected date is within view, so center on that
                        calendarDayToShow = currentlySelectedDate;
                    } else {
                        calendarDayToShow = lastVisibleCalendarDay;
                    }
                }
            }
        }

        this.state = state;
        // Save states parameters
        calendarMode = state.calendarMode;
        firstDayOfWeek = state.firstDayOfWeek;
        minDate = state.minDate;
        maxDate = state.maxDate;
        showWeekDays = state.showWeekDays;

        // Recreate adapter
        final CalendarPagerAdapter<?> newAdapter;
        switch (calendarMode) {
            case MONTHS:
                newAdapter = new MonthPagerAdapter(this);
                break;
            case WEEKS:
                newAdapter = new WeekPagerAdapter(this);
                break;
            default:
                throw new IllegalArgumentException("Provided display mode which is not yet implemented");
        }
        if (adapter == null) {
            adapter = newAdapter;
        } else {
            adapter = adapter.migrateStateAndReturn(newAdapter);
        }
        adapter.setShowWeekDays(showWeekDays);
        pager.setAdapter(adapter);
        setRangeDates(minDate, maxDate);

        // Reset height params after mode change
        int tileHeight = showWeekDays ? calendarMode.visibleWeeksCount + DAY_NAMES_ROW
                : calendarMode.visibleWeeksCount;
        pager.setLayoutParams(new LayoutParams(tileHeight));

        setCurrentDate(
                selectionMode == SELECTION_MODE_SINGLE && !adapter.getSelectedDates().isEmpty()
                        ? adapter.getSelectedDates().get(0)
                        : CalendarDay.today());

        if (calendarDayToShow != null) {
            pager.setCurrentItem(adapter.getIndexForDay(calendarDayToShow));
        }

        invalidateDecorators();
        updateUi();
    }

    private static void enableView(final View view, final boolean enable) {
        view.setEnabled(enable);
        view.setAlpha(enable ? 1f : 0.1f);
    }
}
