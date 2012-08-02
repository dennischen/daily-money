package com.bottleworks.dailymoney.data;

import java.util.Calendar;

public class ScheduleJob {
    Calendar initDate;
    Long repeat;

    public Calendar getInitDate() {
        if(initDate == null) {
            initDate = Calendar.getInstance();
            int currentHour = initDate.get(Calendar.HOUR_OF_DAY);
            if(currentHour > 3) {
                initDate.add(Calendar.DATE, 1);
            }
            initDate.set(Calendar.HOUR_OF_DAY, 3);
            initDate.set(Calendar.MINUTE, 0);
            initDate.set(Calendar.SECOND, 0);
        }
        return initDate;
    }

    public void setInitDate(Calendar initDate) {
        this.initDate = initDate;
    }

    public Long getRepeat() {
        return repeat;
    }

    public void setRepeat(Long repeat) {
        this.repeat = repeat;
    }
}
