package com.example.fitnesstracker.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateManager {
    private Calendar calendar;
    private SimpleDateFormat displayFormat;
    private SimpleDateFormat databaseFormat;

    public DateManager() {
        calendar = Calendar.getInstance();
        displayFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        databaseFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    }

    public String getCurrentDateFormatted() {
        return databaseFormat.format(calendar.getTime());
    }

    public String getFormattedDate() {
        return displayFormat.format(calendar.getTime());
    }

    public void nextDay() {
        calendar.add(Calendar.DAY_OF_MONTH, 1);
    }

    public void previousDay() {
        calendar.add(Calendar.DAY_OF_MONTH, -1);
    }

    public void setDate(int year, int month, int day) {
        calendar.set(year, month, day);
    }
} 