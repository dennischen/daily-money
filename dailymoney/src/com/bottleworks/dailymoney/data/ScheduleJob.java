package com.bottleworks.dailymoney.data;

import java.util.Calendar;

public class ScheduleJob {
    Calendar initDate;
    Long repeat;

    public Calendar getInitDate() {
        if(initDate == null) {
            initDate = Calendar.getInstance();
            initDate.set(Calendar.YEAR, 2011);
            initDate.set(Calendar.MONTH, Calendar.JANUARY);
            initDate.set(Calendar.DAY_OF_MONTH, 3);
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
