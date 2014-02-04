package com.bottleworks.dailymoney.data;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class ScheduleJob {

    final static long INTERVAL_DAY = 86400000L;

    public enum Frequency {
        DAILY, WEEKLY, MONTHLY, YEARLY
    }

    /**
     * schedule initial date time
     */
    private Calendar initTime;
    /**
     * job running frequency
     */
    private Frequency frequency;
    /**
     * repeat interval for AlarmManager, according to initTime & frequency
     */
    private Long repeat;
    /**
     * job trigger time for AlarmManager, according to initTime & frequency
     */
    private Calendar triggerTime;
    /**
     * default value is 1. repeat = frequency * times
     */
    private Long times = 1L;

    public void setInitTime(Calendar initTime) {
        this.initTime = initTime;
    }

    public Long getRepeat(Calendar c) {
        if (c == null) {
            c = getTriggerTime();
        }
        if (frequency != null) {
            switch (frequency) {
            case DAILY:
                repeat = INTERVAL_DAY;
                break;
            case WEEKLY:
                repeat = 7 * INTERVAL_DAY;
                break;
            case MONTHLY:
                switch (c.get(Calendar.MONTH)) {
                case 0:
                case 2:
                case 4:
                case 6:
                case 7:
                case 9:
                case 11:
                    repeat = 31 * INTERVAL_DAY;
                    break;
                case 3:
                case 5:
                case 8:
                case 10:
                    repeat = 30 * INTERVAL_DAY;
                    break;
                case 1:
                    if (c != null) {
                        GregorianCalendar gc = new GregorianCalendar();
                        if (gc.isLeapYear(c.get(Calendar.YEAR))) {
                            repeat = 29 * INTERVAL_DAY;
                        } else {
                            repeat = 28 * INTERVAL_DAY;
                        }
                    }
                    break;
                }
                break;
            case YEARLY:
                if (c != null) {
                    GregorianCalendar gc = new GregorianCalendar();
                    if (gc.isLeapYear(c.get(Calendar.YEAR))) {
                        repeat = 366 * INTERVAL_DAY;
                    } else {
                        repeat = 365 * INTERVAL_DAY;
                    }
                }
                break;
            }
            repeat *= times;
        }
        return repeat;
    }

    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public Calendar getTriggerTime() {
        if (initTime == null || frequency == null) {
            return null;
        } else {
            int month = initTime.get(Calendar.MONTH);
            int dayOfMonth = initTime.get(Calendar.DAY_OF_MONTH);
            int hour = initTime.get(Calendar.HOUR);
            int minute = initTime.get(Calendar.MINUTE);
            int dayOfWeek = initTime.get(Calendar.DAY_OF_WEEK);
            Calendar now = Calendar.getInstance();
            triggerTime = Calendar.getInstance();
            switch (frequency) {
            case DAILY:
                triggerTime.set(Calendar.HOUR, hour);
                triggerTime.set(Calendar.MINUTE, minute);
                if (triggerTime.before(now)) {
                    triggerTime.add(Calendar.DATE, 1);
                }
                break;
            case WEEKLY:
                int weekdayDiff = dayOfWeek - now.get(Calendar.DAY_OF_WEEK);
                triggerTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                triggerTime.set(Calendar.HOUR, hour);
                triggerTime.set(Calendar.MINUTE, minute);
                triggerTime.add(Calendar.DATE, weekdayDiff >= 0 ? weekdayDiff : 7 + weekdayDiff);
                if (triggerTime.before(now)) {
                    triggerTime.add(Calendar.DATE, 7);
                }
                break;
            case MONTHLY:
                triggerTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                triggerTime.set(Calendar.HOUR, hour);
                triggerTime.set(Calendar.MINUTE, minute);
                if (triggerTime.before(now)) {
                    triggerTime.add(Calendar.MONTH, 1);
                }
                break;
            case YEARLY:
                triggerTime.set(Calendar.MONTH, month);
                triggerTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                triggerTime.set(Calendar.HOUR, hour);
                triggerTime.set(Calendar.MINUTE, minute);
                if (triggerTime.before(now)) {
                    triggerTime.add(Calendar.YEAR, 1);
                }
                break;
            }
        }
        triggerTime.set(Calendar.SECOND, 0);
        triggerTime.set(Calendar.MILLISECOND, 0);
        return triggerTime;
    }

    /**
     * Set job initial date time.
     * 
     * @param month
     * @param dayOfMonth
     * @param hour
     * @param minute
     */
    public void setInitTime(Integer month, Integer dayOfMonth, int hour, int minute) {
        initTime = Calendar.getInstance();
        if (month != null) {
            initTime.set(Calendar.MONTH, month);
        }
        if (dayOfMonth != null) {
            initTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        }
        initTime.set(Calendar.HOUR, hour);
        initTime.set(Calendar.MINUTE, minute);
        initTime.set(Calendar.SECOND, 0);
        initTime.set(Calendar.MILLISECOND, 0);
    }

    public void setTimes(Long times) {
        this.times = times;
    }

    public Long getTimes() {
        return times;
    }

}
