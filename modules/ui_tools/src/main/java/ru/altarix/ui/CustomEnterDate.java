package ru.altarix.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ru.altarix.ui.tool.h;

/**
 * Created by Владимир on 26.06.2014.
 */
public class CustomEnterDate extends CustomTextViewUnderLine implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private Calendar calendar = Calendar.getInstance();
    private DatePickerDialog mDateDialog;
    private DatePickerDialog.OnDateSetListener l;
    private SimpleDateFormat dateFormatDayOfMonth;
    private boolean monthYearOnly = false;
    private boolean yearOnly = false;
    private boolean isTime;
    private SimpleDateFormat timeFormat;
    private TimePickerDialog mTimeDialog;
    private TimePickerDialog.OnTimeSetListener timeListener;
    private Long limitDateMin;
    private Long limitDateMax;
    private String title;

    public CustomEnterDate(Context context) {
        super(context);
    }

    public CustomEnterDate(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(AttributeSet attrs) {
        super.init(attrs);

        dateFormatDayOfMonth = new SimpleDateFormat("dd MMMM yyyy");
        timeFormat = new SimpleDateFormat("HH:mm");

        getChildAt(0).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog();
            }
        });

    }

    public void setDateFormat(String format){
        dateFormatDayOfMonth = new SimpleDateFormat(format);
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getTitle()
    {
        return title;
    }

    public void showMonthYearOnly(boolean monthYearOnly)
    {
        this.monthYearOnly = monthYearOnly;
    }

    public void showYearOnly(boolean yearOnly)
    {
        this.yearOnly = yearOnly;
    }

    public void setIsTime(boolean isTime){
        this.isTime = isTime;
    }

    public void setDate(Date date){
        if(date == null)
            tvText.setText(mText);
        else
            setDate(date.getTime());
    }
    public void setTime(int hour, int minute){
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        setDate(calendar.getTime());
    }

    public void setDate(long date){
        calendar.setTimeInMillis(date);
        if(isTime){
            tvText.setText(timeFormat.format(calendar.getTime()));
        }else if (monthYearOnly)
            tvText.setText(
                    getResources().getStringArray(R.array.full_month)[calendar.get(Calendar.MONTH)] +
                            " " + calendar.get(Calendar.YEAR));
        else if (yearOnly)
            tvText.setText(
                    "" + calendar.get(Calendar.YEAR));
        else
            tvText.setText(dateFormatDayOfMonth.format(calendar.getTime()));
        invalidateLayout();
    }

    public Date getDate(){
        return calendar.getTime();
    }
    public Calendar getCalendar(){
        return calendar;
    }

    public void showTimeDialog() {
        if(isTime){
            mTimeDialog = new TimePickerDialog(mContext, this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            mDateDialog.show();
        }else{
            if(yearOnly){
                mDateDialog = createDialogExcludeFields(true, true, false);
                mDateDialog.show();
            }else if(monthYearOnly){
                mDateDialog = createDialogExcludeFields(true, false, false);
                mDateDialog.show();
            }else{
                mDateDialog = new DatePickerDialog(mContext, this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                mDateDialog.show();
            }
            // FIXME remove this after
            // don't fix me
            DateTime minDate = new DateTime(1900,1,1,0,0);
            setLimitMin(minDate.getMillis());
            setLimit();
        }

    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(year, monthOfYear, dayOfMonth);
        setDate(calendar.getTime());
        mDateDialog.dismiss();
        if(l !=null)
            l.onDateSet(view, year, monthOfYear, dayOfMonth);
    }

    public void setDataSetListener(DatePickerDialog.OnDateSetListener l){
        this.l = l;
    }
    public void setTimeSetListener(TimePickerDialog.OnTimeSetListener timeListener){
        this.timeListener = timeListener;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        mTimeDialog.dismiss();
        if(timeListener!=null)
            timeListener.onTimeSet(view, hourOfDay, minute);
    }

    //FIXME кастыль
    public void setLimitMin(Long limitDate) {
        this.limitDateMin = limitDate;
    }
    public void setLimitMax(Long limitDate) {
        this.limitDateMax = limitDate;
    }

    private void setLimit() {
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ){
            if(limitDateMin != null){
                mDateDialog.getDatePicker().setMinDate(limitDateMin);
            }
            if(limitDateMax != null){
                mDateDialog.getDatePicker().setMaxDate(limitDateMax);
            }
        }
    }

    private DatePickerDialog createDialogExcludeFields(boolean excludeDay, boolean excludeMonth, boolean excludeYear){

        final DatePickerDialog dpd = new DatePickerDialog(mContext, this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        /*
            Title appearance hard code
         */
        if(title != null){
            TextView tvTitle = new TextView(mContext);
            tvTitle.setText(title);
            tvTitle.setGravity(Gravity.CENTER);
            tvTitle.setPadding(7,7,7,7);
//            tvTitle.setTextAppearance(mContext, android.R.style.TextAppearance_DeviceDefault);
            dpd.setCustomTitle(tvTitle);
        }

        try{
            java.lang.reflect.Field[] datePickerDialogFields = dpd.getClass().getDeclaredFields();
            for (java.lang.reflect.Field datePickerDialogField : datePickerDialogFields) {
                if (datePickerDialogField.getName().equals("mDatePicker")) {
                    datePickerDialogField.setAccessible(true);
                    DatePicker datePicker = (DatePicker) datePickerDialogField.get(dpd);
                    java.lang.reflect.Field[] datePickerFields = datePickerDialogField.getType().getDeclaredFields();
                    for (java.lang.reflect.Field datePickerField : datePickerFields) {
                        Log.i("test", datePickerField.getName());
                        if (excludeDay && "mDaySpinner".equals(datePickerField.getName()))
                            initDayPickerView(datePickerField, datePicker);

                        if (excludeMonth && "mMonthSpinner".equals(datePickerField.getName()))
                            initDayPickerView(datePickerField, datePicker);

                        if (excludeYear && "mYearSpinner".equals(datePickerField.getName()))
                            initDayPickerView(datePickerField, datePicker);
                    }
                }

            }
        }catch(Exception ex){
        }
        return dpd;

    }

    void initDayPickerView(java.lang.reflect.Field datePickerField, DatePicker datePicker){
        try {
            datePickerField.setAccessible(true);
            Object dayPicker = new Object();
            dayPicker = datePickerField.get(datePicker);
            ((View) dayPicker).setVisibility(View.GONE);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
